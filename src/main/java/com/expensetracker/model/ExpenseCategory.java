package com.expensetracker.model;

public sealed interface ExpenseCategory permits
        ExpenseCategory.Food,
        ExpenseCategory.Transport,
        ExpenseCategory.Shopping,
        ExpenseCategory.Health,
        ExpenseCategory.OtherType {

    String value();

    static ExpenseCategory fromValue(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("Expense category required");
        }
        return switch (raw) {
            case "Food" -> new Food();
            case "Transport" -> new Transport();
            case "Shopping" -> new Shopping();
            case "Health" -> new Health();
            case "Other" -> new OtherType();
            default -> throw new IllegalArgumentException("Unsupported expense category: " + raw);
        };
    }

    record Food() implements ExpenseCategory {
        @Override
        public String value() {
            return "Food";
        }
    }

    record Transport() implements ExpenseCategory {
        @Override
        public String value() {
            return "Transport";
        }
    }

    record Shopping() implements ExpenseCategory {
        @Override
        public String value() {
            return "Shopping";
        }
    }

    record Health() implements ExpenseCategory {
        @Override
        public String value() {
            return "Health";
        }
    }

    record OtherType() implements ExpenseCategory {
        @Override
        public String value() {
            return "Other";
        }
    }
}
