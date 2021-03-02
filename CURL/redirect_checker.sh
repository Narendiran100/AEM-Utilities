#!/bin/sh

URL_TEST_FILE="$1"
COUNT=0
PASSED=0
FAILED=0
cat ${URL_TEST_FILE} | (
  while IFS= read line; do
    ((COUNT++))
    row=${line}
    IFS='>'
    read -ra links <<<"$row"
    unset IFS
    redirect_output=($(curl -I --globoff -s -o /dev/null "${links[0]}" -w '%{redirect_url}|%{response_code}|%{url_effective}'))
    IFS='|'
    read -ra redirect <<<"$redirect_output"
    unset IFS
    if [ "${redirect[0]}" = "${links[1]}" ]; then
      echo "OK: Response Code  ${redirect[1]} ${links[0]} -> ${redirect[0]} \n"
      ((PASSED++))
    else
      echo "NOTOK: ${links[0]} -> ${redirect[2]} != ${links[1]} \n"
      ((FAILED++))
    fi
  done
  echo "Total Links checked : ${COUNT}"
  echo "Total Links Passed the test : ${PASSED}"
  echo "Total Links failed the test  : ${FAILED}"
)
