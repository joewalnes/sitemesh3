This is the SiteMesh documentation.

Currently it exists as web-app, however as soon as SiteMesh offline support
has been implemented, it will also be a generated offline website included
in the SiteMesh documentation.

To package this up, you need Buildr from http://buildr.apache.org/

To view:
  buildr sitemesh-docs:run 
  Goto http://localhost:8080/

To publish the public site, you need Google AppEngine Java SDK installed and
a Google Account that is marked as a developer for the 'sitemesh-docs' AppEngine
project. 
  buildr package
  [app-engine-dir]/bin/appcfg.sh update target/sitemesh-docs
