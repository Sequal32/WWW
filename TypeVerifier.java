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

    Float getFloat(String s) {
        try {
            return Float.parseFloat(s);
        }
        catch (Exception e) {
            return null;
        }
    }

    public Object[] getTypes(String[] args, int[] types) {
        if (types == null) {return null;}

        Object[] output = new Object[args.length];

        for (int i = 0; i < types.length - 1; i++) {
            String val = args[i + 1];
            Object parsed;

            switch (types[i]) {
                case 0:
                    parsed = val;
                    break;
                case 1:
                    parsed = getInt(val);
                    break;
                case 2:
                    parsed = getFloat(val);
                    if (parsed == null)
                        Support.setErrorMessage(String.format("%s is invalid, number expected"));
                    break;
                // case 3:

                //     break;
                default:
                    parsed = "";
                    break;
            }

            output[i] = parsed;
        }

        return output;
    }
}