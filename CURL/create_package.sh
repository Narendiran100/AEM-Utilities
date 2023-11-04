#!/bin/bash

HOST_URL="http://localhost:4502";
PACKAGE_NAME="testpackage1";
GROUP_NAME="my_group";
# Check if an input file is provided
if [ "$#" -ne 1 ]; then
  echo "Usage: $0 input_file"
  exit 1
fi

input_file="$1"

# Check if the input file exists
if [ ! -f "$input_file" ]; then
  echo "Input file not found: $input_file"
  exit 1
fi


# Create a package in AEM
curl -u admin:admin -X POST $HOST_URL/crx/packmgr/service/.json/etc/packages/$GROUP_NAME?cmd=create -d packageName=$PACKAGE_NAME -d groupName=$GROUP_NAME


# Initialize an empty JSON array
json_array="["

# Process each line in the input file
while IFS= read -r line; do
  # Remove leading and trailing spaces, if any
  line=$(echo "$line" | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//')
  # Add each formatted JSON object to the array
  json_array+="$(printf "{\"root\" : \"%s\", \"rules\": []}," "$line")" 
  #| sed 's/["\\]/\\&/g')"
done < "$input_file"

# Remove the trailing comma and close the JSON array
json_array="${json_array%,}]"

# Echo the JSON array
echo "$json_array"

# Updating package filters

curl -u admin:admin -X POST $HOST_URL/crx/packmgr/update.jsp -F path=/etc/packages/my_group/testpackage1.zip -F packageName=${PACKAGE_NAME} -F groupName=${GROUP_NAME} -F filter="$json_array" -F '_charset_=UTF-8'
