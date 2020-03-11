package app;

public class TypeVerifier {

    Integer getInt(String s) {
        try {
            return Integer.parseInt(s);
        }
        catch (Exception e) {
            return null;
        }
    }

    Double getDouble(String s) {
        try {
            return Double.parseDouble(s);
        }
        catch (Exception e) {
            return null;
        }
    }

    public Object[] getTypes(String[] args, Types[] types) {
        if (types == null) {return null;}

        Object[] output = new Object[args.length];
        output[0] = args[0];

        for (int i = 0; i < types.length; i++) {
            String val = args[i + 1];
            Object parsed;

            switch (types[i]) {
                case String:
                    parsed = val;
                    break;
                case Int:
                    parsed = getInt(val);
                    if (parsed == null)
                        Support.setErrorMessage(String.format("%s is invalid, integer expected", val));
                    break;
                case Double:
                    parsed = getDouble(val);
                    if (parsed == null)
                        Support.setErrorMessage(String.format("%s is invalid, number expected", val));
                    break;
                case Brand:
                    parsed = Prices.brandExists(val) ? val : null;
                    if (parsed == null)
                        Support.setErrorMessage(String.format("%s is not a valid brand.", val));
                    break;
                case Tier:
                    parsed = Prices.tierExists(val) ? val : null;
                    if (parsed == null)
                        Support.setErrorMessage(String.format("%s is not a valid tier.", val));
                    break;
                // case Date:
                    // break;
                default:
                    parsed = "";
                    break;
            }

            if (Support.wasError())
                return null;

            output[i + 1] = parsed;
        }
        return output;
    }
}