package net.aspw.client.features.command.shortcuts

open class Token

class Literal(val literal: String) : Token()

class StatementEnd : Token()
