package com.sapog87.visual_novel.core.parser;

import com.sapog87.visual_novel.core.story.VariableInfo;
import lombok.Getter;

import java.util.Map;

public class CalculationVisitor extends Visitor {
    @Getter
    private Object result;
    private final Map<String, VariableInfo> variables;

    public CalculationVisitor(Map<String, VariableInfo> variables) {
        this.variables = variables;
    }

    @Override
    void visitId(Id id) {
        if (!variables.containsKey(id.value)) {
            throw new IllegalArgumentException("identifier {" + id + "} not defined");
        }
        result = variables.get(id.value).getValue();
    }

    @Override
    void visitInt(Int num) {
        result = num.value;
    }

    @Override
    void visitReal(Real num) {
        result = num.value;
    }

    @Override
    void visitBool(Bool bool) {
        result = bool.value;
    }

    @Override
    void visitString(MyString str) {
        result = str.value;
    }

    @Override
    void visitBinaryExpr(BinaryExpr binaryExpr) {
        binaryExpr.left.visit(this);
        Object left = result;
        binaryExpr.right.visit(this);
        Object right = result;
        String op = binaryExpr.op;
        if (left instanceof Integer integer1) {
            if (right instanceof Integer integer2) {
                switch (op) {
                    case "+" -> result = integer1 + integer2;
                    case "-" -> result = integer1 - integer2;
                    case "*" -> result = integer1 * integer2;
                    case "/" -> result = integer1 / integer2;
                    case "%" -> result = integer1 % integer2;
                    case ">" -> result = integer1 > integer2;
                    case "<" -> result = integer1 < integer2;
                    case ">=" -> result = integer1 >= integer2;
                    case "<=" -> result = integer1 <= integer2;
                    case "==" -> result = integer1.intValue() == integer2.intValue();
                    case "!=" -> result = integer1.intValue() != integer2.intValue();
                    default -> throw new IllegalArgumentException("unknown operation");
                }
            } else if (right instanceof Double real2) {
                switch (op) {
                    case "+" -> result = integer1 + real2;
                    case "-" -> result = integer1 - real2;
                    case "*" -> result = integer1 * real2;
                    case "/" -> result = integer1 / real2;
                    case "%" -> result = integer1 % real2;
                    case ">" -> result = integer1 > real2;
                    case "<" -> result = integer1 < real2;
                    case ">=" -> result = integer1 >= real2;
                    case "<=" -> result = integer1 <= real2;
                    case "==" -> result = integer1.intValue() == real2.doubleValue();
                    case "!=" -> result = integer1.intValue() != real2.doubleValue();
                    default -> throw new IllegalArgumentException("unknown operation");
                }
            } else {
                throw new IllegalArgumentException("left operand is Integer, right operand must be Integer or Double");
            }
        } else if (left instanceof Double real1) {
            if (right instanceof Integer integer2) {
                switch (op) {
                    case "+" -> result = real1 + integer2;
                    case "-" -> result = real1 - integer2;
                    case "*" -> result = real1 * integer2;
                    case "/" -> result = real1 / integer2;
                    case "%" -> result = real1 % integer2;
                    case ">" -> result = real1 > integer2;
                    case "<" -> result = real1 < integer2;
                    case ">=" -> result = real1 >= integer2;
                    case "<=" -> result = real1 <= integer2;
                    case "==" -> result = real1.doubleValue() == integer2.intValue();
                    case "!=" -> result = real1.doubleValue() != integer2.intValue();
                    default -> throw new IllegalArgumentException("unknown operation");
                }
            } else if (right instanceof Double real2) {
                switch (op) {
                    case "+" -> result = real1 + real2;
                    case "-" -> result = real1 - real2;
                    case "*" -> result = real1 * real2;
                    case "/" -> result = real1 / real2;
                    case "%" -> result = real1 % real2;
                    case ">" -> result = real1 > real2;
                    case "<" -> result = real1 < real2;
                    case ">=" -> result = real1 >= real2;
                    case "<=" -> result = real1 <= real2;
                    case "==" -> result = real1.doubleValue() == real2.doubleValue();
                    case "!=" -> result = real1.doubleValue() != real2.doubleValue();
                    default -> throw new IllegalArgumentException("unknown operation");
                }
            } else {
                throw new IllegalArgumentException("left operand is Double, right operand must be Integer or Double");
            }
        } else if (left instanceof String string1) {
            if (right instanceof String string2) {
                switch (op) {
                    case "==" -> result = string1.equals(string2);
                    case "!=" -> result = !string1.equals(string2);
                    default -> throw new IllegalArgumentException("unknown operation");
                }
            } else {
                throw new IllegalArgumentException("left operand is String, right operand must be String");
            }
        } else if (left instanceof Boolean bool1) {
            if (right instanceof Boolean bool2) {
                switch (op) {
                    case "&&" -> result = bool1 && bool2;
                    case "||" -> result = bool1 || bool2;
                    case "==" -> result = bool1.equals(bool2);
                    case "!=" -> result = !bool1.equals(bool2);
                    default -> throw new IllegalArgumentException("unknown operation");
                }
            } else {
                throw new IllegalArgumentException("left operand is Boolean, right operand must be Boolean");
            }
        } else {
            throw new IllegalArgumentException("left operand must be String or Boolean or Integer or Double");
        }
    }

    @Override
    void visitAssignExpr(AssignExpr assignExpr) {
        assignExpr.id.visit(this);
        Object id = result;
        assignExpr.expr.visit(this);
        Object expr = result;

        String op = assignExpr.op;
        if (id instanceof Integer integer1) {
            if (expr instanceof Integer integer2) {
                switch (op) {
                    case "=" -> result = integer2;
                    case "-=" -> result = integer1 - integer2;
                    case "+=" -> result = integer1 + integer2;
                    case "*=" -> result = integer1 * integer2;
                    case "/=" -> result = integer1 / integer2;
                    case "|=" -> result = integer1 | integer2;
                    case "&=" -> result = integer1 & integer2;
                    case "%=" -> result = integer1 % integer2;
                    default -> throw new IllegalArgumentException("unknown operation");
                }
            } else if (expr instanceof Double real2) {
                switch (op) {
                    case "=" -> result = real2;
                    case "-=" -> result = integer1 - real2;
                    case "+=" -> result = integer1 + real2;
                    case "*=" -> result = integer1 * real2;
                    case "/=" -> result = integer1 / real2;
                    case "%=" -> result = integer1 % real2;
                    default -> throw new IllegalArgumentException("unknown operation");
                }
            } else {
                throw new IllegalArgumentException("variable is Integer, the assigned value must be Integer or Double");
            }
        } else if (id instanceof Double real1) {
            if (expr instanceof Integer integer2) {
                switch (op) {
                    case "=" -> result = integer2;
                    case "-=" -> result = real1 - integer2;
                    case "+=" -> result = real1 + integer2;
                    case "*=" -> result = real1 * integer2;
                    case "/=" -> result = real1 / integer2;
                    case "%=" -> result = real1 % integer2;
                    default -> throw new IllegalArgumentException("unknown operation");
                }
            } else if (expr instanceof Double real2) {
                switch (op) {
                    case "=" -> result = real2;
                    case "-=" -> result = real1 - real2;
                    case "+=" -> result = real1 + real2;
                    case "*=" -> result = real1 * real2;
                    case "/=" -> result = real1 / real2;
                    case "%=" -> result = real1 % real2;
                    default -> throw new IllegalArgumentException("unknown operation");
                }
            } else {
                throw new IllegalArgumentException("variable is Double, the assigned value must be Integer or Double");
            }
        } else if (id instanceof String str1) {
            if (expr instanceof String str2) {
                switch (op) {
                    case "=" -> result = str2;
                    default -> throw new IllegalArgumentException("unknown operation");
                }
            } else {
                throw new IllegalArgumentException("variable is String, the assigned value must be String");
            }
        } else if (id instanceof Boolean bool1) {
            if (expr instanceof Boolean bool2) {
                switch (op) {
                    case "=" -> result = bool2;
                    case "|=" -> result = bool1 || bool2;
                    case "&=" -> result = bool1 && bool2;
                    default -> throw new IllegalArgumentException("unknown operation");
                }
            } else {
                throw new IllegalArgumentException("variable is Boolean, the assigned value must be Boolean");
            }
        } else {
            throw new IllegalArgumentException("variable must be String or Boolean or Integer or Double");
        }
    }
}
