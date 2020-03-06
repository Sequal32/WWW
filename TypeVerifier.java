package app;

public class TypeVerifier {
    Support support;

    TypeVerifier(Support support) {
        this.support = support;
    }

    Integer getInt(String s) {
        try {
            return Integer.parseInt(s);
        }
        catch (Exception e) {
            support.setError(e);
            return null;
        }
    }

    Float getFloat(String s) {
        try {
            return Float.parseFloat(s);
        }
        catch (Exception e) {
            support.setError(e);
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
                case 1:
                    parsed = getInt(val);
                    break;
                case 2:
                    parsed = getFloat(val);
                    break;
                default:
                    parsed = "";
                    break;
            }

            if (parsed == null) {return null;}
            output[i] = parsed;
        }

        return output;
    }
}