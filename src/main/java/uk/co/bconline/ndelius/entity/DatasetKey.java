package uk.co.bconline.ndelius.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class DatasetKey implements Serializable
{
    private static final long serialVersionUID = -3533134137431580642L;

    @Column(name = "PROBATION_AREA_ID")
    private Long probationAreaID;

    @Column(name = "USER_ID")
    private Long userID;

    public DatasetKey()
    {
        super();
    }

    public DatasetKey(Long probationAreaID, Long userID)
    {
        super();
        this.probationAreaID = probationAreaID;
        this.userID = userID;
    }

    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((probationAreaID == null) ? 0 : probationAreaID.hashCode());
        result = prime * result + ((userID == null) ? 0 : userID.hashCode());
        return result;
    }

    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof DatasetKey))
            return false;
        DatasetKey other = (DatasetKey) obj;
        if (probationAreaID == null)
        {
            if (other.probationAreaID != null)
                return false;
        }
        else if (!probationAreaID.equals(other.probationAreaID))
            return false;
        if (userID == null)
        {
            if (other.userID != null)
                return false;
        }
        else if (!userID.equals(other.userID))
            return false;
        return true;
    }

    public Long getProbationAreaID()
    {
        return probationAreaID;
    }

    public void setProbationAreaID(Long probationAreaID)
    {
        this.probationAreaID = probationAreaID;
    }

    public Long getUserID()
    {
        return userID;
    }

    public void setUserID(Long userID)
    {
        this.userID = userID;
    }
}
