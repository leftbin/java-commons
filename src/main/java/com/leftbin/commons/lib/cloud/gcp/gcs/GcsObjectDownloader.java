package com.leftbin.commons.lib.cloud.gcp.gcs;

import com.leftbin.commons.lib.cloud.gcp.gcs.exception.GcsObjectNotFoundException;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class GcsObjectDownloader {
    public static byte[] download(String bucketName, String objectPath) throws GcsObjectNotFoundException {
        Storage storage = StorageOptions.getDefaultInstance().getService();

        BlobId blobId = BlobId.of(bucketName, objectPath);
        Blob blob = storage.get(blobId);

        if (blob == null) {
            throw new GcsObjectNotFoundException();
        }
        return blob.getContent();
    }
}
