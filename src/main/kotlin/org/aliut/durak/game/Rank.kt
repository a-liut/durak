package org.aliut.durak.game

private const val SIX_VALUE = 6
private const val SEVEN_VALUE = 7
private const val EIGHT_VALUE = 8
private const val NINE_VALUE = 9
private const val TEN_VALUE = 10
private const val JACK_VALUE = 11
private const val QUEEN_VALUE = 12
private const val KING_VALUE = 13
private const val ACE_VALUE = 14

enum class Rank(val value: Int) {
    SIX(SIX_VALUE),
    SEVEN(SEVEN_VALUE),
    EIGHT(EIGHT_VALUE),
    NINE(NINE_VALUE),
    TEN(TEN_VALUE),
    JACK(JACK_VALUE),
    QUEEN(QUEEN_VALUE),
    KING(KING_VALUE),
    ACE(ACE_VALUE),
}
