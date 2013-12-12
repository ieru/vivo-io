vivo-io
=======

Import and export of VIVO data (part of agINFRA tools)

This tool allows to port a VIVO instance to a grid  using the import and add new information from a database to a VIVO instance using the export.

INSTRUCTIONS

The import is used to port a VIVO instance to a Grid by command line.

java -jar agVivoImport.jar VIVOInstanceURL user_name password

eg. java -jar agVivoImport.jar  http://vivo.iu.edu/individual/IndianaUniversity/IndianaUniversity.rdf "Alberto Nogales" "password"


The export is used to add new data stored in a database to a VIVO instance. It has to be run as a Java Project as it has been developed for a specifical example. The case study is adding references to a paper stored in a VIVO instance using the ObjectPropery "cites". The point is that you can configure it to retrieve the information you need and add it to your ontology just changing some code.


