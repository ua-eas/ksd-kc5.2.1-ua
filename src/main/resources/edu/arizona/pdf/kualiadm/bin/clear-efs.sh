#!/bin/bash


function main() {
    echo "Clearing efs..."
    rm -rf /huronsaas/$USER/attachments/*
    echo "Efs cleared"
}

main "$@"

