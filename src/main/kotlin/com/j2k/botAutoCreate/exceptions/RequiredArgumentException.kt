package com.j2k.botAutoCreate.exceptions

class RequiredArgumentException : Exception {
    constructor() : super()
    constructor(argumentName: String) : super("\"$argumentName\" argument is required")
}
