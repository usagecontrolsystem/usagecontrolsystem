package it.cnr.iit.ucs.pip;

public final class PIPKeywords {
    /**
     * Whenever a PIP has to retrieve some informations related to an attribute
     * that is stored inside the request, the only information it requires in
     * order to retrieve the value from the request is the category of the
     * attribute
     */
    public static final String EXPECTED_CATEGORY = "EXPECTED_CATEGORY";

    /**
     * Since we have designed a general PIPReader which is able to read basically
     * all the files, we have to tell this PIP all the characteristics of the
     * attribute it is in charge of reading
     */
    public static final String ATTRIBUTE_ID = "ATTRIBUTE_ID";
    public static final String CATEGORY = "CATEGORY";
    public static final String DATA_TYPE = "DATA_TYPE";

    private PIPKeywords() {}

}
