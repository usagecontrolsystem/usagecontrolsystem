### configure UsageControlFramework/conf_local.xml ###
	- mysql user/password
	- pips: simple file based pips are configured.

### configure PEPStandalone ###
	- check dataucscommons.properties
	  select infrastructureType as "dummy".
	  to keep things simpler it used stub implementations for kms and object storage.
	
	- check Policies/PolicyPublish.xml
	  it checks a couple attributes, for example:
	  the "virus" attribute and lets publish the file if the pc has been scanned
	  and it is clean.
	
	- check Requests/RequestPublish.xml.
	  NOTE: change the resource-id to the path of the file you want to be published.
	
	- check Requests/RequestRetrieve.xml.
	  NOTE: it nees an UUID of the resource to be retrieved from the object storage.

### other info ###
The current demo is able to work with the following infrastructure configurations:
	- Dummy : it has stub implementations of kms and object storage.
	  kms returns always the same key.
	  Dummy Object storage creates a directory dummy_storage in the PEPStandalone curernt path.
	  and creates the bundles with name: "<bundle_uuid>.bundle".
	  This uuid has to be put in the RequestRetrieve resource-id.
	
	- AWS : kms and s3 object storage, identity manager etc..
	
	- Openstack : it supports only Barbican KMS.
	  Barbican is used without token auth.
	  Object storage is still missing, for now we can use the dummy object storage.


### How to run the demo ###
	- configure the environment
	- run UCS, PEP
	- modify the RequestPublish.xml with the file path to be published.
	- run publish.sh ( if the policy is satisfied the file will be stored in the object storage )
	- modify the RequestRetrieve.xml with the bundle uuid to be retrieved.
	- run retrieve.sh. the file should be unpacked in the same path as the published file
	  witn name: "${filename}.enc.dec"
	- check with the diff tool if files are equal
