#!/bin/bash
################################################################################
# compare-pdfs.sh: A script to compare pdfs from two different protocol summary
#                  generation runs and alert if any diffs are found
################################################################################
DIR_ONE="/home/u00/ua/uar/irb-migration/rehearsal-1/summary-equivalence/equivalence/pre-threaded"
DIR_TWO="/home/u00/ua/uar/irb-migration/rehearsal-1/summary-equivalence/equivalence/threaded"
TEXT_DIR_ONE="$DIR_ONE/converted-text"
TEXT_DIR_TWO="$DIR_TWO/converted-text"




cleanup() {
    rm -f "$OUTPUT_DIR"/chunk.*
}
trap cleanup EXIT



#
# $1: <chunck_file>
#
function check_pdfs() {
    chunk_file="$1"
}






function main() {
    rm -rf "$TEXT_DIR_ONE"
    mkdir -p "$TEXT_DIR_ONE"

    rm -rf "$TEXT_DIR_TWO"
    mkdir -p "$TEXT_DIR_TWO"

    while IFS= read -r file; do
        filename=$(basename "$file")
        output_file="$TEXT_DIR_ONE/${filename%.*}.txt"
        pdftotext "$file" "$output_file"
    done < <(find "$DIR_ONE" -type f -name '*.pdf')

    while IFS= read -r file; do
        filename=$(basename "$file")
        output_file="$TEXT_DIR_TWO/${filename%.*}.txt"
        pdftotext "$file" "$output_file"
    done < <(find "$DIR_TWO" -type f -name '*.pdf')

    while IFS= read -r file; do
        filename=$(basename "$file")
        result=$(diff "$TEXT_DIR_ONE/$filename" "$TEXT_DIR_TWO/$filename")
        if [[ -n "$result" ]]; then
            echo "'$filename' is not equivalent!"
        fi
    done < <(find "$TEXT_DIR_ONE" -type f -name '*.txt')
}


main $@

