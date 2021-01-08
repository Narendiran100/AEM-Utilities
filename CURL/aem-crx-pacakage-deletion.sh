#!/bin/bash

DRY_RUN="false";
COUNT=0
Delte_Packages (){

curl -u r.narendiran:Naren@1234 -F cmd=delete http://euossaaemautp1.eu.eur.daikintranet:4502/crx/packmgr/service/.json$1



}

Get_Path () {

printf "\nSearching package path : $1 \n"

ALL_PATHS=$( curl -s -u r.narendiran:Naren@1234 "http://euossaaemautp1.eu.eur.daikintranet:4502/bin/querybuilder.json?path=%2fetc%2fpackages&type=nt%3afile&nodename=$1"| jq -r '.hits[].path')


printf "\nAll packages found : $ALL_PATHS \n" 
for SINGLE_PATH in $ALL_PATHS


do
if [[ "$SINGLE_PATH" != *".snapshot"* ]]
then 
printf "\npackage to be deleted : $SINGLE_PATH\n" 

if [  "$DRY_RUN" = false ]
then
COUNT=$((COUNT+1))

Delte_Packages $SINGLE_PATH
printf "__________________________________________________________________________________________________________"
fi
fi
done



}



Get_Path	test.zip






printf "\nTotal packages deleted : $COUNT\n"




