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
CHUNK_DIR="$OUTPUT_DIR/chunk"
PDF_CHUNK_DIR_ONE="$CHUNK_DIR/pdf_chunk_one"
PDF_CHUNK_DIR_TWO="$CHUNK_DIR/pdf_chunk_two"
TEXT_CHUNK_DIR="$CHUNK_DIR/text_chunk"
PDF_FILE_LIST_ONE="$CHUNK_DIR/pdf_file_list_one.txt"
PDF_FILE_LIST_TWO="$CHUNK_DIR/pdf_file_list_two.txt"
TEXT_FILE_LIST="$CHUNK_DIR/text_file_list.txt"


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
    mkdir -p "$TEXT_DIR_ONE"
    mkdir -p "$TEXT_DIR_TWO"
    mkdir -p "$CHUNK_DIR"
    mkdir -p "$PDF_CHUNK_DIR_ONE"
    mkdir -p "$PDF_CHUNK_DIR_TWO"
    mkdir -p "$TEXT_CHUNK_DIR"
    touch "$LOG_FILE"
}


function chunk_pdf_filenames() {
    _chunk_pdf_filenames "$PDF_FILE_LIST_ONE" "$PDF_DIR_ONE" "$PDF_CHUNK_DIR_ONE"
    _chunk_pdf_filenames "$PDF_FILE_LIST_TWO" "$PDF_DIR_TWO" "$PDF_CHUNK_DIR_TWO"
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
    done < <(find "$pdf_dir" -type f -name 'Protocol Summary Report*.pdf')

    # Chunk out a file for each thread that will get forked, e.g. chunk.00.txt chunk.01.txt
    log "Splitting pdf file list: '$pdf_file_list'"
    log "$(wc -l < "$pdf_file_list") entries found"
    split -n l/$THREAD_COUNT -e -d "$pdf_file_list" "$chunk_dir/chunk." --additional-suffix=.txt
}


function run_pdf_converter_threads() {
    log "Converting env one pdfs to text"
    _run_converter_threads "$PDF_CHUNK_DIR_ONE" "$TEXT_DIR_ONE"

    log "Converting env two pdfs to text"
    _run_converter_threads "$PDF_CHUNK_DIR_TWO" "$TEXT_DIR_TWO"
}


# $1: chunks_dir
# $2: text_output_dir
function _run_converter_threads() {
    chunk_dir="$1"
    text_output_dir="$2"

    while IFS= read -r -d '' chunk_file; do
        filename=$(basename "$chunk_file")
        line_count=$(wc -l < "$chunk_file")
        log "Launching convert thread for $filename with $line_count commands(s)"
        convert_pdfs "$chunk_file" "$text_output_dir" &
    done < <(find "$chunk_dir" -maxdepth 1 -type f -name 'chunk*.txt' -print0)

    wait
}


# $1: chunk_file
# $2: output_text_dir
function convert_pdfs() {
    chunk_file="$1"
    output_text_dir="$2"

    while IFS= read -r pdf_file; do
        filename=$(basename "$pdf_file")
        text_file="$output_text_dir/${filename%.*}.txt"
        pdftotext "$pdf_file" "$text_file"
    done < "$chunk_file"
}


function chunk_text_file_names() {
    while IFS= read -r file; do
        filename=$(basename "$file")
        echo "$filename" >> "$TEXT_FILE_LIST"
    done < <(find "$TEXT_DIR_ONE" -type f -name '*.txt')

    log "Splitting text file list: '$TEXT_FILE_LIST'"
    log "$(wc -l < "$TEXT_FILE_LIST") entries found"
    split -n l/$THREAD_COUNT -e -d "$TEXT_FILE_LIST" "$TEXT_CHUNK_DIR/chunk." --additional-suffix=.txt
}


function run_text_diff_threads() {
    while IFS= read -r -d '' chunk_file; do
        filename=$(basename "$chunk_file")
        line_count=$(wc -l < "$chunk_file")
        log "Launching diff thread for $filename with $line_count commands(s)"
        diff_text_files "$chunk_file" &
    done < <(find "$TEXT_CHUNK_DIR" -maxdepth 1 -type f -name 'chunk*.txt' -print0)

    wait
}


# $1: chunk_file
function diff_text_files() {
    chunk_file="$1"

    # Diff the two converted text files
    while IFS= read -r text_file; do
        result=$(diff "$TEXT_DIR_ONE/$text_file" "$TEXT_DIR_TWO/$text_file")
        if [[ -n "$result" ]]; then
            log "ERROR: '$text_file' is not equivalent!"
        fi
    done < "$chunk_file"
}


function main() {
    log "Setting up file system"
    setup_file_system

    log "Chunking pdf filenames"
    chunk_pdf_filenames

    log "Running converter threads"
    run_pdf_converter_threads

    log "Chunking text filenames"
    chunk_text_file_names

    log "Running diff threads"
    run_text_diff_threads

    log "All comparisons complete."
}

main $@
