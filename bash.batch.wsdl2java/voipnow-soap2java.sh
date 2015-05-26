# 4PSA VoipNow - The bash script for transforming schemes into Java code.
# Copyright (c) 2012 Rack-Soft (www.4psa.com). All rights reserved.

#!/bin/bash

# Script for generating from the schemes the jar files which need to be added in the Java project

 # Please modify the variables which are set to CHANGEME:
 # * YOUR_JAVA_HOME - the path to the directory where java is installed; if JAVA_HOME environment variable is not set, YOUR_JAVA_HOME variable will be used
 # * YOUR_AXIS2_HOME - the path to the directory where axis2 is installed; if AXIS2_HOME environment variable is not set, YOUR_AXIS2_HOME variable will be used
 # * YOUR_ANT_HOME - the path to the directory where ant is installed; if ANT_HOME environment variable is not set, YOUR_ANT_HOME variable will be used
 # * PATH_TO_SCHEMES - the path to the directory where the schemes are placed
 # * IP - the ip of the VoipNow server
 # * YOUR_WSDL2JAVA_OUTPATH - the path to the directory where you will find the generated jar files after the script will run

 # You should change YOUR_WSDL_NS2P_VALUES variable only if you have some different namespace variabels (ns1, ns2, etc.); otherwise the plugin will not be aware of them and therefore it will not use them.

YOUR_JAVA_HOME=CHANGEME
YOUR_AXIS2_HOME=CHANGEME
YOUR_ANT_HOME=CHANGEME
PATH_TO_SCHEMES=CHANGEME
IP=CHANGEME
YOUR_WSDL2JAVA_OUTPATH=CHANGEME
YOUR_WSDL_NS2P_VALUES="ns1=http://4psa.com/ResellerMessages.xsd/3.0.0,ns2=http://4psa.com/HeaderData.xsd/3.0.0"
# ,xsi=http://www.w3.org/2001/XMLSchema-instance DOES NOT WORK


# Finds the paths to the wsdl files present in every module.
# Sets them into the WSDL_MODULE_FILES array.
function setWsdlModuleFiles {
	for f in "${PATH_TO_SCHEMES}/*"
	do
		# The wsdl file for each module 
		local MODULE_WSDL="${f}/*.wsdl"
		local COUNT="0"
		for wsdl in $MODULE_WSDL 
		do
			# Check if it is a file 
			if [ -f "$wsdl" ] 
			then
				# Save the module name in WSDL_MODULE_FILES array 
				WSDL_MODULE_FILES[$COUNT]=$wsdl
				COUNT=$[$COUNT+1]									
			fi				
		done		
	done
}

# Finds the path to the voipnowservice.wsdl.
# Set it into the WSDL_VOIPNOW_FILE variable
function setWsdlVoipnowFile {
	for f in "${PATH_TO_SCHEMES}/*"
	do
		# All the wsdl files from the root of the schemes folder
		local WSDLS="${f}.wsdl"
		for wsdl in $WSDLS 
		do
			# Check if it is a file
			if [ -f "$wsdl" ] 
			then
				WSDL_VOIPNOW_FILE=$wsdl				
			fi				
		done		
	done	
}

# Comments the <service> tag in a wsdl file provided as its first parameter
function commentServiceTag {
	sed -i 's/<service/<!--<service/' $1
	sed -i 's/<\/service>/<\/service>-->/' $1	
}

# Modifies the voipnowservice.wsdl file: adds the <service> tags from each module's wsdl file.
function addServiceTagsInRootWsdl {
	# Set the path to the voipnowservice.wsdl in the $WSDL_VOIPNOW_FILE variable
	setWsdlVoipnowFile
	
	ports=( Account ServiceProvider Organization User Extension Channel PBX Billing Report GlobalOp )
	namespaces=( account serviceProvider organization user extension channel pbx billing report globalop )
	agents=( account sp organization user extension channel pbx billing report globalop )
	serviceTemplate="<service name=\"{port}Port\"><port name=\"{port}Port\" binding=\"{namespace}:{port}\"><soap:address location=\"https://$IP/soap2/{agent}_agent.php\"/></port></service>"
	
	# Remove the ending </definitions> tag from the temporary file
	sed -i '/\/definitions/d' $WSDL_VOIPNOW_FILE
	
	# Generate the <service> tags for each module and add it into the voipnowservice.wsdl file
	for i in {0..9}
	do
		service=${serviceTemplate//\{namespace\}/${namespaces[i]}}
		service=${service//\{port\}/${ports[i]}}
		service=${service//\{agent\}/${agents[i]}}
		echo $service >> $WSDL_VOIPNOW_FILE
	done

	# Add back the ending </definitions> tag to the temporary file
	echo "</definitions>" >> $WSDL_VOIPNOW_FILE	
}



echo "=========================================="
echo "ENVIRONMENTALS"
echo "=========================================="
# Testing if JAVA_HOME evironment variable is already set.
if [ "${JAVA_HOME}" = "" ]
then 
	# If not, set it with the value given above
	export JAVA_HOME=$YOUR_JAVA_HOME
		
	# Test the provided value for $YOUR_JAVA_HOME
	if [ "${JAVA_HOME}" = "" ]
	then
		#ERROR: JAVA_HOME is not set.
		echo "The JAVA_HOME environment variable is not defined."
		echo "You can define it either by editing this script or"
		echo "by setting it in Command Line Interface."
		echo "Example: $ export JAVA_HOME=/usr/lib/Java"
		exit 1
	fi
fi

# Testing whether java.exe exists
if [ !-f "${JAVA_HOME}/bin/java" ]
then
	echo "The JAVA_HOME environment variable is not defined correctly."
	echo "This environment variable is needed to run this program."
	echo "JAVA_HOME should point to a JDK/JRE folder."
	export JAVA_HOME=
	exit 1
fi


#==========================================

# Test the AXIS2_HOME environment variable
if [ "${AXIS2_HOME}" = "" ]
then
	# If not set, we set it with the value given above
	export AXIS2_HOME=$YOUR_AXIS2_HOME
	# Test the provided value for $YOUR_AXIS2_HOME
	if [ "${AXIS2_HOME}" = "" ] 
	then
		# ERROR: AXIS2_HOME is not seted.
		echo "The AXIS2_HOME environment variable is not defined."
		echo "You can define it either by editing this script or"
		echo "by setting it in Command Line Interface."
		echo "Example: $ export AXIS2_HOME=/usr/lib/apache-axis"
		exit 1
	fi
fi

# Test the bin folder
if [ !-f "${AXIS2_HOME}/bin/axis2.sh" ] 
then 
	echo "The AXIS2_HOME environment variable is not defined correctly"
	echo "This environment variable is needed to run this script."
	export AXIS2_HOME=
	exit 1
fi


#==========================================

# Test the ANT_HOME environment variable
if [ "${ANT_HOME}" = "" ]
then
	# If not set, set it with the value given above
	export ANT_HOME=$YOUR_ANT_HOME
	# Test the provided value for $YOUR_ANT_HOME
	if [ "${ANT_HOME}" = "" ]
	then
		# ERROR: ANT_HOME is not set.
		echo "The ANT_HOME environment variable is not defined."
		echo "You can define it either by editing this script or"
		echo "by setting it in Command Line Interface."
		echo "Example: C:> export AXIS2_HOME=/usr/lib/apache-ant"
		exit 1
	fi
fi

# Test the bin folder
if [ !-f "%ANT_HOME%/bin/ant" ]
then
	echo "The ANT_HOME environment variable is not defined correctly"
	echo "This environment variable is needed to run this script."
	export ANT_HOME=
	exit 1
fi

echo "using JAVA_HOME  = $JAVA_HOME"
echo "using AXIS2_HOME = $AXIS2_HOME"
echo "using ANT_HOME   = $ANT_HOME"



echo "=========================================="
echo "GENERATING JAVA SOURCES"
echo "=========================================="
# Transform the schemes into Java code

# Copy the schemes in a temporary folder
cp -R $PATH_TO_SCHEMES "${PATH_TO_SCHEMES}_temp"	
# Work with the temporary schemes
PATH_TO_SCHEMES=${PATH_TO_SCHEMES}_temp

# Set the wsdl files from each module in WSDL_MODULE_FILES array
setWsdlModuleFiles
	
# Comment the <service> tag from each wsdl module file 	
moduleFilesNr=${#WSDL_MODULE_FILES[@]}
INDEX=0
while [ $INDEX -lt $moduleFilesNr ] 
do
	moduleFile=${WSDL_MODULE_FILES[$INDEX]}
	commentServiceTag $moduleFile
	INDEX=$[$INDEX+1]		
done
	
# Add all the <service>	tags in the voipnowservice.wasl file
addServiceTagsInRootWsdl	
		
# Call wsdl2java script
sh $AXIS2_HOME/bin/wsdl2java.sh -d xmlbeans -uri $WSDL_VOIPNOW_FILE -ns2p $YOUR_WSDL_NS2P_VALUES -u -or -s -o $YOUR_WSDL2JAVA_OUTPATH		



echo "=========================================="
echo "RUNNING ANT FOR COMPILING"
echo "=========================================="
$ANT_HOME/bin/ant -f $YOUR_WSDL2JAVA_OUTPATH/build.xml	

#Remove the temp folder 
rm -rf $PATH_TO_SCHEMES



echo "=========================================="
echo "DONE"
echo "=========================================="
echo "You can find the jar files in folder: $YOUR_WSDL2JAVA_OUTPATH/build/lib"