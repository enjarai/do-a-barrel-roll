package nl.enjarai.doabarrelroll.math;

public abstract class Parser {
    protected final String string;
    protected int pos = -1;
    protected char ch;

    protected Parser(String string) {
        this.string = string;
    }

    protected void nextChar() {
        ch = ++pos < string.length() ? string.charAt(pos) : 0;
    }

    protected boolean weat(char charToEat) {
        while (Character.isWhitespace(ch)) nextChar();
        return eat(charToEat);
    }

    protected boolean eat(char charToEat) {
        if (ch == charToEat) {
            nextChar();
            return true;
        }
        return false;
    }

    protected boolean isVariableChar() {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '-' || ch == '_' || ch == ':';
    }

    public String getString() {
        return string;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Parser parser = (Parser) o;

        return string.equals(parser.string);
    }

    @Override
    public int hashCode() {
        return string.hashCode();
    }
}
