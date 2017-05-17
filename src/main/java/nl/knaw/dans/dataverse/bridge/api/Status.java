package nl.knaw.dans.dataverse.bridge.api;

/**
 * Created by akmi on 16/05/17.
 */
public enum Status {
        NOT_ARCHIVED_YET,
        FAILED,
        PROGRESS,
        ARCHIVED;

    public String toString(){
        switch(this){
            case FAILED:
                return "FAILED";
            case PROGRESS :
                return "PROGRESS";
            case ARCHIVED :
                return "ARCHIVED";
        }
        return "NOT_ARCHIVED_YET";//default
    }

}
