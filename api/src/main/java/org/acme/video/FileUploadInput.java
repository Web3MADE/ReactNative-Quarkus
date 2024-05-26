package org.acme.video;

import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

public class FileUploadInput {
    // values are URL decoded by default
    // form field name is specified in the @FormParam annotation
    // MAY need to change to RestForm if can't upload to Azure
    @RestForm("video")
    public FileUpload video;

    @RestForm("thumbnail")
    public FileUpload thumbnail;

    @RestForm("title")
    public String title;

    @RestForm("uploaderId")
    public Long uploaderId;
}

