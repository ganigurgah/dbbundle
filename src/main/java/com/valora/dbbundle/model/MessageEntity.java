package com.valora.dbbundle.model;

import jakarta.persistence.*;

import java.io.Serializable;

/**
 * Base interface for message entities.
 * Applications should implement this interface in their own entity class.
 */
public interface MessageEntity extends Serializable {

    String getMsgKey();

    String getMsgValue();

    String getLocaleCode();
}
