package dev.khbd.interp4j.intellij.common.grammar.fmt;

import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * Format specifier in format expression.
 *
 * <p>Note: Only conversion is required by grammar. Any other part can be absent.
 *
 * @author Sergei_Khadanovich
 */
@Value
@AllArgsConstructor
public class FmtSpecifier implements FmtExpressionPart {

    Index index;
    String flags;
    Integer width;
    Integer precision;
    Conversion conversion;
    Position position;

    FmtSpecifier(Conversion conversion, Position position) {
        this(null, null, null, null, conversion, position);
    }

    FmtSpecifier(Index index, Conversion conversion, Position position) {
        this(index, null, null, null, conversion, position);
    }

    @Override
    public FmtExpressionPartKind kind() {
        return FmtExpressionPartKind.SPECIFIER;
    }

    @Override
    public void visit(FmtExpressionVisitor visitor) {
        visitor.visitSpecifierPart(this);
    }

    public boolean isPercent() {
        return conversion.getSymbols().equals("%");
    }

    public boolean isNewLine() {
        return conversion.getSymbols().equals("n");
    }
}
