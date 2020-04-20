package app;

import java.text.SimpleDateFormat;

public class TypeVerifier {
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyy");

    DataManager data;

    TypeVerifier(DataManager data) {
        this.data = data;
    }

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


    public Object[] getTypes(String[] args, Types[] types) {
        if (types == null) {return null;}

        Object[] output = new Object[args.length];
        output[0] = args[0];

        for (int i = 1; i < args.length; i++) {
            String val = args[i];
            Object parsed = "";

            if (i - 1 >= types.length) {
                output[i] = val;
                continue;
            }

            switch (types[i - 1]) {
                case String:
                    parsed = val;
                    break;
                case Int:
                    parsed = getInt(val);
                    if (parsed == null)
                        Support.setErrorMessage(String.format("%s is invalid, integer expected", val));
                    break;
                case Float:
                    parsed = getFloat(val);
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
                case Date:
                    parsed = Support.getDate(val);
                    if (parsed == null)
                        Support.setErrorMessage(String.format("%s is not a valid date, the format is %s.", val, dateFormat.toPattern()));
                    break;
                case Client:
                    Integer clientNumber = getInt(val);
                    if (clientNumber == null)
                        {Support.setErrorMessage("Not a valid client number."); break;}
                    parsed = data.getClient(clientNumber);
                    if (parsed == null) 
                        Support.setErrorMessage("Client does not exist in the database!");
                    break;
                case Order:
                    Integer orderNumber = getInt(val);
                    if (orderNumber == null)
                        {Support.setErrorMessage("Not a valid order number."); break;}
                    parsed = data.getOrder(orderNumber);
                    if (parsed == null) 
                        Support.setErrorMessage("Order does not exist in the database!");
                    break;
                default:
                    parsed = val;
            }

            if (Support.wasError())
                return null;

            output[i] = parsed;
        }
        return output;
    }
}