#!/bin/bash
################################################################################
# check-pdfs.sh
#
# A script to check the protocol summary pdfs to see if the protocol number in the filename
# matches the protocol number in the pdf file converted to text
# This will help catch cases where there is a potential race condition
# where the protocol summaries code could have saved a different protocol summary than the protocol in the filname
################################################################################

# Change these for machine specific paths
PDF_DIR="/huronsaas/irb-migration/2021.06.04/attachments"
OUTPUT_DIR="/huronsaas/irb-migration/validation/protocol-summary"

# How many threads should be forked
THREAD_COUNT=8

# These shouldn't have to change
LOG_FILE="$OUTPUT_DIR/check-pdfs.log"
CHUNK_DIR="$OUTPUT_DIR/chunk"
PDF_CHUNK_DIR="$CHUNK_DIR/pdf_chunk"
PDF_FILE_LIST="$CHUNK_DIR/pdf_file_list.txt"
CONVERTED_TEXT_DIR="$OUTPUT_DIR/converted-text"

# Delete dynamically generated output upon any type of exit
function cleanup() {
    rm -rf "$CHUNK_DIR"
}
trap cleanup EXIT


function timestamp(){
    echo -n "$(date '+%F %T')"
}


function log() {
    # shellcheck disable=SC2145
    echo "$(timestamp) $@" | tee -a "$LOG_FILE"
}


function setup_file_system() {
    rm -rf "$OUTPUT_DIR"
    mkdir -p "$OUTPUT_DIR"
    mkdir -p "$CHUNK_DIR"
    mkdir -p "$PDF_CHUNK_DIR"
    mkdir -p "$CONVERTED_TEXT_DIR"
    touch "$LOG_FILE"
}


function chunk_pdf_filenames() {
    _chunk_pdf_filenames "$PDF_FILE_LIST" "$PDF_DIR" "$PDF_CHUNK_DIR"
}


# $1: pdf_file_list
# $2: pdf_dir
# #3: chunk_dir
function _chunk_pdf_filenames() {
    pdf_file_list="$1"
    pdf_dir="$2"
    chunk_dir="$3"

    while IFS= read -r file; do
        echo "$file" >> "$pdf_file_list"
    done < <(find "$pdf_dir" -type f -user 'kualiadm' -name 'Protocol Summary Report*.pdf')

    # Chunk out a file for each thread that will get forked, e.g. chunk.00.txt chunk.01.txt
    log "Splitting pdf file list: '$pdf_file_list'"
    log "$(wc -l < "$pdf_file_list") entries found"
    split -n l/$THREAD_COUNT -e -d "$pdf_file_list" "$PDF_CHUNK_DIR/chunk." --additional-suffix=.txt
}

function run_check_protocol_summary_threads() {
    while IFS= read -r -d '' chunk_file; do
        filename=$(basename "$chunk_file")
        line_count=$(wc -l < "$chunk_file")
        log "Launching check protocol summary thread for $filename with $line_count commands(s)"
        check_protocol_number_in_filename_to_number_in_file "$chunk_file" &
    done < <(find "$PDF_CHUNK_DIR" -maxdepth 1 -type f -name 'chunk*.txt' -print0)

    wait
}


# $1: chunk_file
function check_protocol_number_in_filename_to_number_in_file() {
    chunk_file="$1"
    echo "$chunk_file"

    while IFS= read -r pdf_file; do
      filename=$(basename "$pdf_file")
      protocol_num_from_filename=$(echo "$filename" | awk '{print $4}' | awk -F'.pdf' '{print $1}')
      output_file="$CONVERTED_TEXT_DIR/$filename.txt"
      pdftotext "$pdf_file" "$output_file"
      value_to_search_for="Protocol #: $protocol_num_from_filename"

      if ! grep -q "$value_to_search_for" "$output_file" ;then
        log "ERROR: $value_to_search_for is not in $pdf_file"
      fi
    done < "$chunk_file"
}


function main() {
    log "Setting up file system"
    setup_file_system

    log "Chunking pdf filenames"
    chunk_pdf_filenames

    log "Running check protocol summary threads"
    run_check_protocol_summary_threads

    log "All comparisons complete."
}

main $@
