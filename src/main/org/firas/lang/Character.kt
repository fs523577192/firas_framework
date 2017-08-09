package org.firas.lang

/**
 *
 */
class Character {

    companion object {

        /**
         * The minimum radix available for conversion to and from strings.
         * The constant value of this field is the smallest value permitted
         * for the radix argument in radix-conversion methods such as the
         * {@code digit} method, the {@code forDigit} method, and the
         * {@code toString} method of class {@code Integer}.
         *
         * @see     Character#digit(char, int)
         * @see     Character#forDigit(int, int)
         * @see     Integer#toString(int, int)
         * @see     Integer#valueOf(String)
         */
        val MIN_RADIX = 2

        /**
         * The maximum radix available for conversion to and from strings.
         * The constant value of this field is the largest value permitted
         * for the radix argument in radix-conversion methods such as the
         * {@code digit} method, the {@code forDigit} method, and the
         * {@code toString} method of class {@code Integer}.
         *
         * @see     Character#digit(char, int)
         * @see     Character#forDigit(int, int)
         * @see     Integer#toString(int, int)
         * @see     Integer#valueOf(String)
         */
        val MAX_RADIX = 36

        /**
         * Determines the character representation for a specific digit in
         * the specified radix. If the value of {@code radix} is not a
         * valid radix, or the value of {@code digit} is not a valid
         * digit in the specified radix, the null character
         * ({@code '\u005Cu0000'}) is returned.
         * <p>
         * The {@code radix} argument is valid if it is greater than or
         * equal to {@code MIN_RADIX} and less than or equal to
         * {@code MAX_RADIX}. The {@code digit} argument is valid if
         * {@code 0 <= digit < radix}.
         * <p>
         * If the digit is less than 10, then
         * {@code '0' + digit} is returned. Otherwise, the value
         * {@code 'a' + digit - 10} is returned.
         *
         * @param   digit   the number to convert to a character.
         * @param   radix   the radix.
         * @return  the {@code char} representation of the specified digit
         *          in the specified radix.
         * @see     Character#MIN_RADIX
         * @see     Character#MAX_RADIX
         * @see     Character#digit(char, int)
         */
        fun forDigit(digit: Int, radix: Int): Char {
            if (digit >= radix || digit < 0) return 0.toChar()
            if (radix < MIN_RADIX || radix > MAX_RADIX) return 0.toChar()
            if (digit < 10) return digit.plus('0'.toInt()).toChar()
            return digit.minus(10).plus('A'.toInt()).toChar()
        }

        fun isDigit(codePoint: Int): Boolean {
            return codePoint in '0'.toInt() .. '9'.toInt() ||
                    codePoint in 'A'.toInt() .. 'Z'.toInt() ||
                    codePoint in 'a'.toInt() .. 'z'.toInt() ||
                    codePoint in 0xFF10 .. 0xFF19 ||
                    codePoint in 0xFF21 .. 0xFF3A ||
                    codePoint in 0xFF41 .. 0xFF5A
        }

        fun isDigit(ch: Char): Boolean {
            return isDigit(ch.toInt())
        }

        /**
         * Returns the numeric value of the specified character (Unicode
         * code point) in the specified radix.
         *
         * <p>If the radix is not in the range {@code MIN_RADIX} &le;
         * {@code radix} &le; {@code MAX_RADIX} or if the
         * character is not a valid digit in the specified
         * radix, {@code -1} is returned. A character is a valid digit
         * if at least one of the following is true:
         * <ul>
         * <li>The character is one of the uppercase Latin letters
         *     {@code 'A'} through {@code 'Z'} and its code is less than
         *     {@code radix + 'A' - 10}.
         *     In this case, {@code ch - 'A' + 10}
         *     is returned.
         * <li>The character is one of the lowercase Latin letters
         *     {@code 'a'} through {@code 'z'} and its code is less than
         *     {@code radix + 'a' - 10}.
         *     In this case, {@code ch - 'a' + 10}
         *     is returned.
         *     <li>The character is one of the fullwidth uppercase Latin letters A
         *     ({@code '\u005CuFF21'}) through Z ({@code '\u005CuFF3A'})
         *     and its code is less than
         *     {@code radix + '\u005CuFF21' - 10}.
         *     In this case,
         *     {@code codePoint - '\u005CuFF21' + 10}
         *     is returned.
         * <li>The character is one of the fullwidth lowercase Latin letters a
         *     ({@code '\u005CuFF41'}) through z ({@code '\u005CuFF5A'})
         *     and its code is less than
         *     {@code radix + '\u005CuFF41'- 10}.
         *     In this case,
         *     {@code codePoint - '\u005CuFF41' + 10}
         *     is returned.
         * </ul>
         * @param   codePoint the character (Unicode code point) to be converted.
         * @param   radix   the radix.
         * @return  the numeric value represented by the character in the
         *          specified radix.
         */
        fun digit(codePoint: Int, radix: Int): Int {
            if (radix < MIN_RADIX || radix > MAX_RADIX) return -1
            var base = '0'.toInt()
            if (codePoint in base .. '9'.toInt()) {
                var result = codePoint - base
                return if (result >= radix) -1 else result
            }
            base = 'A'.toInt()
            if (codePoint in base .. 'Z'.toInt()) {
                var result = codePoint - base + 10
                return if (result >= radix) -1 else result
            }
            base = 'a'.toInt()
            if (codePoint in base .. 'z'.toInt()) {
                var result = codePoint - base + 10
                return if (result >= radix) -1 else result
            }
            base = 0xFF10
            if (codePoint in base .. 0xFF19) {
                var result = codePoint - base
                return if (result >= radix) -1 else result
            }
            base = 0xFF21
            if (codePoint in base .. 0xFF3A) {
                var result = codePoint - base + 10
                return if (result >= radix) -1 else result
            }
            base = 0xFF41
            if (codePoint in base .. 0xFF5A) {
                var result = codePoint - base + 10
                return if (result >= radix) -1 else result
            }
            return -1
        }

        /**
         * Returns the numeric value of the character {@code ch} in the
         * specified radix.
         * <p>
         * If the radix is not in the range {@code MIN_RADIX} &le;
         * {@code radix} &le; {@code MAX_RADIX} or if the
         * value of {@code ch} is not a valid digit in the specified
         * radix, {@code -1} is returned. A character is a valid digit
         * if at least one of the following is true:
         * <ul>
         * <li>The character is one of the uppercase Latin letters
         *     {@code 'A'} through {@code 'Z'} and its code is less than
         *     {@code radix + 'A' - 10}.
         *     In this case, {@code ch - 'A' + 10}
         *     is returned.
         * <li>The character is one of the lowercase Latin letters
         *     {@code 'a'} through {@code 'z'} and its code is less than
         *     {@code radix + 'a' - 10}.
         *     In this case, {@code ch - 'a' + 10}
         *     is returned.
         *     <li>The character is one of the fullwidth uppercase Latin letters A
         *     ({@code '\u005CuFF21'}) through Z ({@code '\u005CuFF3A'})
         *     and its code is less than
         *     {@code radix + '\u005CuFF21' - 10}.
         *     In this case,
         *     {@code codePoint - '\u005CuFF21' + 10}
         *     is returned.
         * <li>The character is one of the fullwidth lowercase Latin letters a
         *     ({@code '\u005CuFF41'}) through z ({@code '\u005CuFF5A'})
         *     and its code is less than
         *     {@code radix + '\u005CuFF41'- 10}.
         *     In this case,
         *     {@code codePoint - '\u005CuFF41' + 10}
         *     is returned.
         * </ul>
         * @param   ch      the character to be converted.
         * @param   radix   the radix.
         * @return  the numeric value represented by the character in the
         *          specified radix.
         */
        fun digit(ch: Char, radix: Int): Int {
            return digit(ch.toInt(), radix)
        }

    }
}