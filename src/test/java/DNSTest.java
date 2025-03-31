import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HexFormat;

public class DNSTest {
    // https://en.wikipedia.org/wiki/Domain_Name_System
    // https://mislove.org/teaching/cs4700/spring11/handouts/project1-primer.pdf
    // https://www.ibm.com/docs/en/qradar-on-cloud?topic=data-parsing-dns-query-response-fields
    // https://support.huawei.com/enterprise/en/doc/EDOC1100174721/f917b5d7/dns
    public static void main(String[] args) throws Exception {
        String hexData = "7D EE 00 10 00 01 00 00 00 00 00 01 1E 31 6C 6E 67 76 32 78 6C 73 67 39 38 65 78 34 36 35 69 38 6E 38 6C 30 79 63 70 69 73 36 68 03 63 71 6F 02 63 68 00 00 01 00 01 00 00 29 04 D0 00 00 80 00 00 00";

        /*
        Example data:
        7D EE 00 10 00 01 00 00 => ID 7D EE, Flags 00 10, QCOUNT 00 01, ANCOUNT 00 00
        00 00 00 01 1E 31 6C 6E => NSCOUNT 00 00, ARCOUNT 00 01, 1E = 31 bytes
        67 76 32 78 6C 73 67 39
        38 65 78 34 36 35 69 38
        6E 38 6C 30 79 63 70 69
        73 36 68 03 63 71 6F 02 => 3 bytes, 2 bytes
        63 68 00 00 01 00 01 00 => Query type 00 01, Query class 00 01
        00 29 04 D0 00 00 80 00
        00 00
         */

        HexFormat hexFormat = HexFormat.ofDelimiter(" ");
        byte[] bytes = hexFormat.parseHex(hexData);
        System.out.println(new String(bytes));

        // Parse headers
        // -> read first 12 bytes
        ByteBuffer headerBuffer = ByteBuffer.wrap(bytes, 0, 12);
        short id = headerBuffer.getShort(0);
        short flags = headerBuffer.getShort(2);
        short qCount = headerBuffer.getShort(4);
        short anCount = headerBuffer.getShort(6);
        short nsCount = headerBuffer.getShort(8);
        short arCount = headerBuffer.getShort(10);
        String header = """
                        ID      = %s
                        Flags   = %s
                        QDCOUNT = %s
                        ANCOUNT = %s
                        NSCOUNT = %s
                        ARCOUNT = %s
                        """.formatted(
                String.format("%04X", id),
                String.format("%04X", flags),
                String.format("%04X", qCount),
                String.format("%04X", anCount),
                String.format("%04X", nsCount),
                String.format("%04X", arCount)
        );

        System.out.println(header);
        String flagBitstring = String.format("%016d", Integer.parseInt(Integer.toBinaryString(flags)));
        System.out.println("Flag bits:\n" + flagBitstring);
        System.out.printf(
                """
                from bitstring:
                QR = %s
                OPCODE = %s
                AA = %s
                TC = %s
                RD = %s
                RA = %s
                Z = %s
                AD = %s
                CD = %s
                RCODE = %s%n""",
                flagBitstring.charAt(0),
                flagBitstring.substring(1, 5),
                flagBitstring.charAt(5),
                flagBitstring.charAt(6),
                flagBitstring.charAt(7),
                flagBitstring.charAt(8),
                flagBitstring.charAt(9),
                flagBitstring.charAt(10),
                flagBitstring.charAt(11),
                flagBitstring.substring(12, 16)
        );

        int rcode = flags & 0xf;
        int CD = (flags >>> 4) & 1;
        int AD = (flags >>> 5) & 1;
        int Z = (flags >>> 6) & 1;
        int RA = (flags >>> 7) & 1;
        int RD = (flags >>> 8) & 1;
        int TC = (flags >>> 9) & 1;
        int AA = (flags >>> 10) & 1;
        int opcode = (flags >>> 11) & 0xf;
        int qr = (flags >>> 15) & 1;
        System.out.printf(
                """
                from shifting:
                QR = %d
                OPCODE = %d
                AA = %d
                TC = %d
                RD = %d
                RA = %d
                Z = %d
                AD = %d
                CD = %d
                RCODE = %d%n""", qr, opcode, AA, TC, RD, RA, Z, AD, CD, rcode
        );

        // Parse question
        ByteBuffer questionBuffer = ByteBuffer.wrap(bytes, 12, bytes.length - 12);
        do {
            int length = questionBuffer.get() & 0xff;
            System.out.printf("read %d bytes from bitstring: ", length);
            if (length == 0) break;
            StringBuilder name = new StringBuilder();
            for (int i = 0; i < length; i++) {
                char c = (char) (questionBuffer.get() & 0xff);
                System.out.print(c);
                name.append(c);
            }
            System.out.println("\n" + name.toString());
        } while (questionBuffer.hasRemaining());

        short queryType = questionBuffer.getShort();
        short queryClass = questionBuffer.getShort();
        System.out.printf("\nQuery type: %d (%s)\n", queryType, getTypeName(queryType));
        System.out.printf("Query class: %d (%s)\n", queryClass, getClassName(queryClass));

        System.out.printf("Remaining bytes: %d", questionBuffer.remaining());

    }

    public static String getTypeName(int type) {
        switch (type) {
            case 1: return "A";
            case 2: return "NS";
            case 5: return "CNAME";
            case 6: return "SOA";
            case 12: return "PTR";
            case 13: return "HINFO";
            case 15: return "MX";
            case 16: return "TXT";
            case 17: return "RP";
            case 18: return "AFSDB";
            case 24: return "SIG";
            case 25: return "KEY";
            case 28: return "AAAA";
            case 29: return "LOC";
            case 33: return "SRV";
            case 252: return "AXFR";
            case 253: return "MAILB";
            case 254: return "MAILA";
            case 255: return "*";
        }
        return "Unknown (" + type + ")";
    }

    public static String getClassName(int type) {
        switch (type) {
            case 1: return "IN";
            case 2: return "CS";
            case 3: return "CH";
            case 4: return "SH";
            case 255: return "*";
        }
        return "Unknown (" + type + ")";
    }
}
