package com.expensetracker.model;

public sealed interface IncomeCategory permits
        IncomeCategory.Salary,
        IncomeCategory.Freelance,
        IncomeCategory.Investment,
        IncomeCategory.OtherType {

    String value();

    static IncomeCategory fromValue(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("Income category required");
        }
        return switch (raw) {
            case "Salary" -> new Salary();
            case "Freelance" -> new Freelance();
            case "Investment" -> new Investment();
            case "Other" -> new OtherType();
            default -> throw new IllegalArgumentException("Unsupported income category: " + raw);
        };
    }

    record Salary() implements IncomeCategory {
        @Override
        public String value() {
            return "Salary";
        }
    }

    record Freelance() implements IncomeCategory {
        @Override
        public String value() {
            return "Freelance";
        }
    }

    record Investment() implements IncomeCategory {
        @Override
        public String value() {
            return "Investment";
        }
    }

    record OtherType() implements IncomeCategory {
        @Override
        public String value() {
            return "Other";
        }
    }
}
