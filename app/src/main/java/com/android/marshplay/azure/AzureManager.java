/**
 * ----------------------------------------------------------------------------------
 * Microsoft Developer & Platform Evangelism
 * <p>
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * <p>
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * ----------------------------------------------------------------------------------
 * The example companies, organizations, products, domain names,
 * e-mail addresses, logos, people, places, and events depicted
 * herein are fictitious.  No association with any real company,
 * organization, product, domain name, email address, logo, person,
 * places, or events is intended or should be inferred.
 * ----------------------------------------------------------------------------------
 **/

package com.android.marshplay.azure;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.LinkedList;

public class AzureManager {
    static final String validChars = "abcdefghijklmnopqrstuvwxyz";
    private static final String storageConnectionString =
            "DefaultEndpointsProtocol=https;" +
                    "AccountName=wowmiwebblob;" +
                    "AccountKey=USjwTKk/AsOj8e+SOMxY0ZazciyMzko7cWjPXKQjNLjFmwOBZmvm2xMMGg4BE7FzptA+Q7PbWkMQ4i3PXC4HHw==;" +
                    "EndpointSuffix=core.windows.net";
    static SecureRandom rnd = new SecureRandom();


    private static String containerName = "wowmi";
    public static String BASE_AZURE_URL = "https://wowmiwebblob.blob.core.windows.net/" + containerName + "/";

    private static CloudBlobContainer getContainer() throws Exception {
        CloudStorageAccount storageAccount = CloudStorageAccount
                .parse(storageConnectionString);
        CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
        CloudBlobContainer container = blobClient.getContainerReference(containerName);
        return container;
    }

    public static String uploadFile(String fileName, InputStream file, int imageLength) throws Exception {
        CloudBlobContainer container = getContainer();
        container.createIfNotExists();
        CloudBlockBlob imageBlob = container.getBlockBlobReference(fileName);
        imageBlob.upload(file, imageLength);
        return fileName;

    }


    public static String[] ListBlobs() throws Exception {
        CloudBlobContainer container = getContainer();

        Iterable<ListBlobItem> blobs = container.listBlobs();

        LinkedList<String> blobNames = new LinkedList<>();
        for (ListBlobItem blob : blobs) {
            blobNames.add(((CloudBlockBlob) blob).getName());
        }

        return blobNames.toArray(new String[blobNames.size()]);
    }

    public static void GetFile(String name, OutputStream imageStream, long fileLength) throws Exception {
        CloudBlobContainer container = getContainer();
        CloudBlockBlob blob = container.getBlockBlobReference(name);
        if (blob.exists()) {
            blob.downloadAttributes();
            fileLength = blob.getProperties().getLength();
            blob.download(imageStream);
        }
    }

    static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(validChars.charAt(rnd.nextInt(validChars.length())));
        return sb.toString();
    }

}
