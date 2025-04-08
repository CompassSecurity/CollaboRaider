# Description
The extension [Collaborator Everywhere](https://github.com/portswigger/collaborator-everywhere) is quite popular in the BApp store.
Initially created as a proof-of-concept for [Portswigger's blog post](https://portswigger.net/research/cracking-the-lens-targeting-https-hidden-attack-surface) on hidden server-side vulnerabilities, it helps discovering SSRF vulnerabilities by automatically injecting Collaborator URLs in various headers and creates issue if one of these URLs is resolved or fetched by the server.
All in all, it is a great tool in any pentester's toolbox.

However, despite its popularity, the extension is quite old and not maintained, with the last commit three years ago.
There is no easy way to edit payloads (issue [#2](https://github.com/PortSwigger/collaborator-everywhere/issues/2) and [#21](https://github.com/PortSwigger/collaborator-everywhere/issues/21), except downloading the source, editing the resource file and building it.
To my surprise, the extension is not only capable of modifying HTTP header fields, but also supports URL parameters and can use the host header from the original request as an additional placeholder.
But without a convenient way to add, edit or enable payloads, this feature is 
However, one open issue ([#5](https://github.com/PortSwigger/collaborator-everywhere/issues/5)) addresses further lack of configuration and suggests more placeholders.

This motivated me to rewrite the extension using the new Montoya API and add a few new features that I missed during previous pentesting assessments.
In particular, Portswigger's example project repository contains a great [demo project](https://github.com/PortSwigger/burp-extensions-montoya-api-examples/tree/main/collaborator) that explains how to work with a custom Collaborator client, which served as starting point for this extension.

# Features
All features from the original [Collaborator Everywhere](https://github.com/portswigger/collaborator-everywhere) extension are covered:
- Insert a freshly generated Collaborator payload to certain headers in every request to an in-scope host
- Raise an issue if a request to one of these Collaborator URLs has been received
- Display detailed information about the request in the issue and which payload caused it
- Show time difference between the original request and the received interaction
- Check own external IP address and add a disclaimer if the interaction was received from the own address
- Includes payloads to modify HTTP header fields and URL parameters
- Payloads can be constructed with a placeholder that is replaces with the Host header value of the original request

New features:
- Payloads can be added, modified, deleted, enabled or disabled in a new tab 
- All received interactions are displayed in a new tab similar to the built-in Collaborator
- Requests that caused an interaction are highlighted in the Proxy history
- Added placeholders to use the value of the Origin and Referer header fields in a payload
- Interactions and settings are stored persistently in the project file
- Extension is based on the new Montoya API

# Installation
## Manual Installation
In Burp Suite, open the `Extensions` tab and navigate to the `Installed` sub-tab. There, select the extension type `Java` and select the Jar file.
## From BApp Store
hopefully coming soon...

# Usage
todo

# License
See [LICENSE](LICENSE) file (MIT License).

# Author
- Andreas Brombach (GitHub: [dec1m0s](https://github.com/dec1m0s))

