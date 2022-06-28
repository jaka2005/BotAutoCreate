package com.j2k.botAutoCreate.exceptions

class UnexpectedResponseException : Exception {
    constructor() : super()
    constructor(response: String) : super("Response \"$response\" is not expected")
}