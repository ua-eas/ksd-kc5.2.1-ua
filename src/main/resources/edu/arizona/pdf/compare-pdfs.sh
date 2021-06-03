#!/bin/bash
################################################################################
# compare-pdfs.sh
#
# A script to compare pdfs from two different protocol summary generation runs
# and alert if any diffs are found. This script assumes that PDF_DIR_ONE and
# PDF_DIR_ONE contaim the same number of pdfs, and the same named pdfs.
################################################################################

# Change these for machine specific paths
PDF_DIR_ONE="$HOME/ua/uar/irb-migration/rehearsal-1/summary-equivalence/equivalence/pre-threaded"
PDF_DIR_TWO="$HOME/ua/uar/irb-migration/rehearsal-1/summary-equivalence/equivalence/threaded"
OUTPUT_DIR="$HOME/ua/uar/irb-migration/rehearsal-1/summary-equivalence/pdf-text"

# How many threads should be forked
THREAD_COUNT=8

# These shouldn't have to change
LOG_FILE="$OUTPUT_DIR/compare-pdfs.log"
TEXT_DIR_ONE="$OUTPUT_DIR/env-one/converted-text"
TEXT_DIR_TWO="$OUTPUT_DIR/env-two/converted-text"
CHUNK_DIR="$OUTPUT_DIR/chunks"
MASTER_FILE_LIST="$CHUNK_DIR/master_file_list.txt"


#
# Delete dynamically generated output upon any type of exit
#
cleanup() {
    rm -rf "$CHUNK_DIR"
}
trap cleanup EXIT


#
# $1: <chunck_file>
#
# This is the function that will get forked into multiple threads, and its the
# workhorse logic of this script.
#
function check_pdfs() {
    chunk_file="$1"

    # Convert all the pdfs for env-1
    while IFS= read -r filename; do
        pdf_file="$PDF_DIR_ONE/$filename"
        text_file="$TEXT_DIR_ONE/${filename%.*}.txt"
        pdftotext "$pdf_file" "$text_file"
    done < "$chunk_file"

    # Convert all the pdfs for env-2
    while IFS= read -r filename; do
        pdf_file="$PDF_DIR_TWO/$filename"
        text_file="$TEXT_DIR_TWO/${filename%.*}.txt"
        pdftotext "$pdf_file" "$text_file"
    done < "$chunk_file"

    # Diff the two converted text files
    while IFS= read -r filename; do
        text_file="${filename%.*}.txt"
        result=$(diff "$TEXT_DIR_ONE/$text_file" "$TEXT_DIR_TWO/$text_file")
        if [[ -n "$result" ]]; then
            log "ERROR: '$filename' is not equivalent!"
        fi
    done < "$chunk_file"
}


function chunk_filenames() {
    # Create a master file list based on files from env one
    while IFS= read -r file; do
        filename=$(basename "$file")
        echo "$filename" >> "$MASTER_FILE_LIST"
    done < <(find "$PDF_DIR_ONE" -type f -name '*.pdf')

    # Chunk out a file for each thread that will get forked, e.g. chunk.00.txt chunk.01.txt
    split -n l/$THREAD_COUNT -e -d "$MASTER_FILE_LIST" "$CHUNK_DIR/chunk." --additional-suffix=.txt
}


function setup_dirs() {
    rm -rf "$OUTPUT_DIR"
    mkdir -p "$OUTPUT_DIR"
    mkdir -p "$TEXT_DIR_ONE"
    mkdir -p "$TEXT_DIR_TWO"
    mkdir -p "$CHUNK_DIR"
    touch "$LOG_FILE"
}


function start_threads() {
    while IFS= read -r -d '' chunk_file; do
        file_name=$(basename "$chunk_file")
        line_count=$(wc -l < "$chunk_file")
        log "Launching thread for $file_name with $line_count commands(s)"
        check_pdfs "$chunk_file" &
    done < <(find "$CHUNK_DIR" -maxdepth 1 -type f -name 'chunk*.txt' -print0)
}


function timestamp(){
    echo -n "$(date '+%F %T')"
}


function log() {
    # shellcheck disable=SC2145
    echo "$(timestamp) $@" | tee -a "$LOG_FILE"
}


function main() {
    setup_dirs

    log "Starting..."
    chunk_filenames
    start_threads

    # Wait for all the threads to join back
    wait

    log "All comparisons complete."
}

main $@
