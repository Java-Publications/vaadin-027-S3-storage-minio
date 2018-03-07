# vaadin-027-S3-storage-minio
How to use Vaadin together with a Amazon S3 Compatible Cloud Storage 

## Demo-App
The demo App is a blob - storage based on the minio storage and the Vaadin Framework
App to present the blobs. 

Raspberry Pi will take images, triggered by TinkerForge Motion Sensor, 
store it in Minio Storage. Vaadin will give you a image gallery.



## S3 Storage - Minio

The project wee are using here is on github [https://github.com/minio/minio](https://github.com/minio/minio)
With this we can start building a document storage for our Vaadin Apps.

from the minio page itself : 
>It is best suited for storing unstructured data such 
as photos, videos, log files, backups and container / VM images. 
Size of an object can range from a few KBs to a maximum of 5TB.

But first, I will show how to start with the 
S3 Storage itself.
We have to install Docker, for this example. 
The docker installation itself is out of the scope of this
article. After you have done this, we could start 
polling the image with the docker command. ```docker pull minio/minio```

Now we can start using this. For thee first examples 
I would create a container without external volume, ever data will 
be lost after the container is stopped and deleted.
For the first steps, this is perfect ;-)

```docker run -p 9000:9000 --name minio minio/minio server /data```

But if you want/need persistent volumes, up to 16 volumes per 
minio node can be used.

``` 
docker run -p 9000:9000 --name minio \
  -v /mnt/data1:/data1 \
  -v /mnt/data2:/data2 \
  -v /mnt/data3:/data3 \
  -v /mnt/data4:/data4 \
  -v /mnt/data5:/data5 \
  -v /mnt/data6:/data6 \
  -v /mnt/data7:/data7 \
  -v /mnt/data8:/data8 \
  minio/minio server /data1 /data2 /data3 /data4 /data5 /data6 /data7 /data8
```

After thee first start (in this example I just used the first version)
you will get the information about the node via the logs.

```
Drive Capacity: 50 GiB Free, 60 GiB Total

Endpoint:  http://172.17.0.2:9000  http://127.0.0.1:9000
AccessKey: 1TEQLU3S6N19ID4A32QJ 
SecretKey: KQamn/OWyGZPnuGq+1ZNYgRZqJLeiAJ06bJwNmJ9 

Browser Access:
   http://172.17.0.2:9000  http://127.0.0.1:9000

Command-line Access: https://docs.minio.io/docs/minio-client-quickstart-guide
   $ mc config host add myminio http://172.17.0.2:9000 1TEQLU3S6N19ID4A32QJ KQamn/OWyGZPnuGq+1ZNYgRZqJLeiAJ06bJwNmJ9

Object API (Amazon S3 compatible):
   Go:         https://docs.minio.io/docs/golang-client-quickstart-guide
   Java:       https://docs.minio.io/docs/java-client-quickstart-guide
   Python:     https://docs.minio.io/docs/python-client-quickstart-guide
   JavaScript: https://docs.minio.io/docs/javascript-client-quickstart-guide
   .NET:       https://docs.minio.io/docs/dotnet-client-quickstart-guide
```

Important are the following information's

* Endpoint:  http://172.17.0.2:9000  http://127.0.0.1:9000
* AccessKey: 1TEQLU3S6N19ID4A32QJ 
* SecretKey: KQamn/OWyGZPnuGq+1ZNYgRZqJLeiAJ06bJwNmJ9
 
With this you can start, using the node that was just created.
Use a web browser and access the following url:  **http://172.17.0.2:9000**

![_data/_images/minio_first_login_01.png](_data/_images/minio_first_login_01.png)

For the first login, use the **AccessKey** and **SecretKey** that was created
by the minio server itself.

![_data/_images/minio_first_login_02.png](_data/_images/minio_first_login_02.png)

After the first login, there will be an more or less empty screen.
![_data/_images/minio_after_first_login.png](_data/_images/minio_after_first_login.png)

Now we have all preparations done on this side. 
Time to start with the Java-side as well.

## S3 Storage - Minio - Java-SDK
The Java SDK is provided at maven central. 

```xml
<dependency>
    <groupId>io.minio</groupId>
    <artifactId>minio</artifactId>
    <version>3.0.12</version>
</dependency>
```

The first steps are done by the 
Minio - Client, provided by teh SDK.

````ava
      final MinioClient minioClient = new MinioClient(
                                       "https://localhost:9000", 
                                       "1TEQLU3S6N19ID4A32QJ", 
                                       "KQamn/OWyGZPnuGq+1ZNYgRZqJLeiAJ06bJwNmJ9");
```` 

Now we can play around with the api itself.

## Vaadin App
