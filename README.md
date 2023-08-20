

# Dx 프로젝트
[Dicom 이미지 변환](#dicom-이미지-변환)  
[PDF 변환](#pdf-생성)  
[Azure 설정](#azure-설정)  
[CI/CD 설정](#cicd-설정)  
[기타 설정](#기타-설정)  

- dicom: 의료용 디지털 영상 및 통신 표준 (디지털 영상이니 이미지도 당연히 포함되어 있다.)  
- keyvault: config server로 이해하면 된다. (민감한 정보를 소스 상에 노출하지 않기 위해 설정한다.)

## Dicom 이미지 변환
1. weasis-core-img-4.5.3 의존성인 opencv_java.dll 또는 libopencv_java.so 파일 설치한다.  
아래 URL로 들어가서 OS와 weasis 버전에 맞는 opencv_java를 설치한다.  
[opencv_java.dll](https://github.com/nroduit/mvn-repo/blob/master/org/weasis/thirdparty/org/opencv/opencv_java/4.5.3-dcm/opencv_java-4.5.3-dcm-windows-x86-64.dll)  
리눅스의 경우 아래 라이브러리 추가 설치 필요  
https://github.com/nroduit/mvn-repo/tree/master/org/weasis/thirdparty/com/sun/media/libclib_jiio/1.2-b04 
1. windows의 경우 시스템 환경 변수인 Path에 opencv_java.dll 폴더를 설정한다.  
linux는 LD_LIBRARY_PATH에 디렉토리를 포함한다.
1. pom.xml 설정한다.
1. dicom 이미지 변환한다.  

pom.xml 설정  
file로 설정한 이유는 dcm4che를 제공하는 maven repository 사이트가 가끔씩 shutdown되어 다운 받지 못하는 현상으로 인해 local repository로 변경함
```xml
	<repositories>
		<repository>
			<id>dcm4che</id>
			<name>dicom library</name>
			<url>file://${project.basedir}/lib</url>
		</repository>
	</repositories>
	<dependencies>
		<dependency>
			<groupId>org.dcm4che</groupId>
			<artifactId>dcm4che-core</artifactId>
			<version>${dcm4che.version}</version>
		</dependency>
		<dependency>
			<groupId>org.dcm4che</groupId>
			<artifactId>dcm4che-dict</artifactId>
			<version>${dcm4che.version}</version>
		</dependency>
		<dependency>
			<groupId>org.dcm4che</groupId>
			<artifactId>dcm4che-image</artifactId>
			<version>${dcm4che.version}</version>
		</dependency>
		<dependency>
			<groupId>org.dcm4che</groupId>
			<artifactId>dcm4che-imageio</artifactId>
			<version>${dcm4che.version}</version>
		</dependency>
		<dependency>
			<groupId>org.dcm4che</groupId>
			<artifactId>dcm4che-imageio-opencv</artifactId>
			<version>${dcm4che.version}</version>
		</dependency>
		<dependency>
			<groupId>org.dcm4che</groupId>
			<artifactId>dcm4che-imageio-rle</artifactId>
			<version>${dcm4che.version}</version>
		</dependency>
		<dependency>
			<groupId>org.weasis.core</groupId>
			<artifactId>weasis-core-img</artifactId>
			<version>${weasis.core.img.version}</version>
		</dependency>
```

Image Registry에 Dicom을 등록한다.  
```java
public class DicomImageConverter {
    static Logger logger = LoggerFactory.getLogger(DicomImageConverter.class);
    static boolean loaded = false;

    // 반드시 target/classes/lib/opencv 디렉토리 및 라이브러리가 존재해야 함
    // 다른 방법이 있으면 입맛대로 구현해도 됨
    static {
        try {
            if (loaded) {
                init();
                loaded = true;
            }
        } catch (Exception e) {
            logger.error("{}", e);
        }
    }

    public static void init() throws Exception {
        IIORegistry registry = IIORegistry.getDefaultInstance();
        registry.registerServiceProvider(new DicomImageReaderSpi());
        registry.registerServiceProvider(new NativeJ2kImageReaderSpi());
        registry.registerServiceProvider(new NativeJLSImageReaderSpi());
        registry.registerServiceProvider(new NativeJPEGImageReaderSpi());
    }
```

Dicom 이미지를 변환한다.  
```java
    public static BufferedImage convert(InputStream is) throws IOException {
        ImageInputStream iis = ImageIO.createImageInputStream(is); 
        Iterator<ImageReader> iter = ImageIO.getImageReadersByFormatName("dicom");
        if (!iter.hasNext()) {
            throw new UnsupportedOperationException("no supported image reader: dicom");
        }

        DicomImageReader ir = (DicomImageReader) iter.next();
        ir.setInput(iis, false);

        DicomImageReadParam irp = (DicomImageReadParam) ir.getDefaultReadParam();

        // only read first image (first image 0)
        BufferedImage bi = ir.read(0, irp);
        ir.close();

        return bi;
    }
```

Jpeg 파일로 변환한다.
```java
public class MultipartFileToJpegImage {
    static Logger logger = LoggerFactory.getLogger(MultipartFileToJpegImage.class);

    public static Image convert(MultipartFile file) {
        try {
            String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
            if (ext == null) {
                return Image.exception(new UnsupportedOperationException("unknown extension"), multipartFileTransferToOutputStream(file));
            }

            BufferedImage bi = null;
            switch (ext.toLowerCase()) {
                case "dcm":
                    bi = DicomImageConverter.convert(file.getInputStream());
                    break;
                case "jpg":
                    bi = BypassBufferedImage.convert(file.getInputStream());
                    break;
                default:
                    bi = BypassBufferedImage.convert(file.getInputStream());
                    break;
            }

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(bi, "jpg", os);
            return Image.valueOf(bi, os);
        } catch (Exception e) {
            return Image.exception(e, multipartFileTransferToOutputStream(file));
        }
    }

    private static final int DEFAULT_BUFFER_SIZE = 8192;

    public static OutputStream multipartFileTransferToOutputStream(MultipartFile file) {
        // MultipartFile to OutputStream
    }
}
```

## PDF 생성
1. wkhtmltopdf 설치
1. xvfb 설치  
1. html to pdf 변환

windows  
https://wkhtmltopdf.org/downloads.html

ubuntu  
xvfb: Container 상에서 Html을 PDF로 변환하기 때문에 가상의 화면이 필요하다. xvfb가 가상의 화면 역할을 한다.
```bash
apt install -y xvfb wkhtmltopdf
```

```java
public class HtmlToPdf {
    private static String xvfb;
    private static String wkhtmltopdf;

    public static void init(String xvfb, String wkhtmltopdf) {
        HtmlToPdf.xvfb = xvfb;
        HtmlToPdf.wkhtmltopdf = wkhtmltopdf;
    }
    
    public static byte[] convert(String html) throws Exception {
        try {
            WrapperConfig wrapperConfig = new WrapperConfig(wkhtmltopdf);
            if (!"none".equals(xvfb)) {
                XvfbConfig xvfbConfig = new XvfbConfig(xvfb);
                wrapperConfig.setXvfbConfig(xvfbConfig);
            }
            Pdf pdf = new Pdf(wrapperConfig);
            pdf.addPageFromString(html);
            pdf.setAllowMissingAssets();
            byte[] bytes = pdf.getPDF();
            return bytes;
        } catch (Exception e) {
            throw e;
        }
    }
}

```

## Azure 설정
1. application.yml 설정
1. pom.xml 설정
1. Azure 소스 설정

application.yml
```yaml
spring:
  cloud:
    azure:
      storage:
        blob:
          account-name: ${spring.cloud.azure.storage.blob.account-name}
          account-key: ${spring.cloud.azure.storage.blob.account-key}
          endpoint: ${spring.cloud.azure.storage.blob.endpoint}
      keyvault:
        enabled: false
        endpoint: ${spring.cloud.azure.keyvault.secret.endpoint}
        profile:
          tenant-id: ${spring.cloud.azure.keyvault.secret.profile.tenant-id}
        credential:
          client-id: ${spring.cloud.azure.keyvault.secret.credential.client-id}
          client-secret: ${spring.cloud.azure.keyvault.secret.credential.client-secret}
```

pom.xml  
keyvault 및 storage 설정
```xml
		<dependency>
			<groupId>com.azure.spring</groupId>
			<artifactId>spring-cloud-azure-starter-keyvault</artifactId>
		</dependency>
		<dependency>
			<groupId>com.azure.spring</groupId>
			<artifactId>spring-cloud-azure-starter-storage</artifactId>
		</dependency>
```

### Blob Storage 설정
```java
public class AzureBlobStorageServiceImpl implements StorageService {
    private final String CONAINTER_NAME = "blob-container";

    private BlobContainerClient blobContainerClient;

    public AzureBlobStorageServiceImpl(AzureStorageBlobProperties storageProperties) {
        StorageSharedKeyCredential credential =  new StorageSharedKeyCredential(
            storageProperties.getAccountName(), 
            storageProperties.getAccountKey());
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
            .endpoint(storageProperties.getEndpoint())
            .credential(credential)
            .buildClient();

        this.blobContainerClient = blobServiceClient.getBlobContainerClient(CONAINTER_NAME);
    }

    @Override
    public void upload(String blobName, byte[] bytes) throws Exception {
        BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
        blobClient.upload(BinaryData.fromBytes(bytes));
    }

    @Override
    public byte[] download(String blobName) throws Exception {
        BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
        byte[] bytes = blobClient.downloadContent().toBytes();
        return bytes;
    }

    @Override
    public void delete(String blobName) throws Exception {
        BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
        blobClient.delete();
    }
}
```

### Keyvalut 설정
```java
public class RedisTemplateConfiguration {
    @Bean
    @Primary
    @ConfigurationProperties("spring.redis")
    RedisProperties redisProperties(
            // azure keyvault
            @Value("${spring-redis-password}") String password) {
        RedisProperties redisProperties = new RedisProperties();
        redisProperties.setPassword(password);
        return redisProperties;
    }
}
```

## CI/CD 설정
1. container 기본 이미지 Dockerfile
1. container 실행 이미지 설정
1. maven 빌드 및 azure 배포

container 기본 이미지 Dockerfile  
- xvbf 및 wkhtmltopdf를 설치
```docker
FROM ubuntu:18.04

RUN apt update -y

# Install OpenSSH and set the password for root to "Docker!". In this example, "apk add" is the install instruction for an Alpine Linux-based image.
RUN apt install -y openssh-server openssh-client
RUN ssh-keygen -A
RUN echo "root:Docker!" | chpasswd

# Copy the sshd_config file to the /etc/ssh/ directory
COPY sshd_config /etc/ssh/
RUN mkdir -p /run/sshd

# Install vim
RUN apt install -y vim

RUN echo "\nset linebreak" >> /etc/vim/vimrc

# Install netcat and traceroute
RUN apt install -y net-tools
RUN apt install -y netcat
RUN apt install -y traceroute

# Install java and wkhtmltopdf
RUN apt install -y openjdk-8-jre xvfb wkhtmltopdf
RUN rm -rf /var/lib/apt/lists/*
```

sshd_config  
azure web service로 ssh 접속하여 log를 확인하기 위한 설정
```apacheconf
# This is ssh server systemwide configuration file.
#
# /etc/sshd_config

Port                    2222
ListenAddress           0.0.0.0
LoginGraceTime          180
X11Forwarding           yes
Ciphers                 aes128-cbc,3des-cbc,aes256-cbc,aes128-ctr,aes192-ctr,aes256-ctr
MACs                    hmac-sha1,hmac-sha1-96
StrictModes             yes
SyslogFacility          DAEMON
PasswordAuthentication  yes
PermitEmptyPasswords    no
PermitRootLogin         yes
Subsystem               sftp internal-sftp
```

container 실행 이미지 설정  
entrypoint.sh
```bash
#!/bin/bash

# Export utf-8
export LC_ALL=C.UTF-8

# Get environment variables to show up in SSH session
eval $(printenv | sed -n "s/^\([^=]\+\)=\(.*\)$/export \1=\2/p" | sed 's/"/\\\"/g' | sed '/=/s//="/' | sed 's/$/"/' >> /etc/profile)

# starting sshd process
echo "start sshd"
/usr/sbin/sshd

echo "start java"
java -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} \
    -Duser.timezone=${USER_TIMEZONE} \
    -Dfile.encoding=UTF-8 \
    -Djava.library.path=/app/opencv \
    -jar /app/ROOT.jar
```

pom.xml    
- jib-maven-plugin: Container Image를 생성
- build-helper-maven-plugin: Maven Build 시간을 설정
- azure-webapp-maven-plugin: Azure Web Service로 배포하기 위한 설정
```xml
			<plugin>
				<groupId>com.google.cloud.tools</groupId>
				<artifactId>jib-maven-plugin</artifactId>
				<version>3.1.4</version>
				<configuration>
					<containerizingMode>packaged</containerizingMode>
					<from>
						<image>${docker.image.prefix}/${docker.image.name}:base</image>
						<auth>
							<username>${docker.username}</username>
							<password>${docker.password}</password>
						</auth>
					</from>
					<to>
						<image>${docker.image.prefix}/${docker.image.name}</image>
						<tags>${docker.tag}</tags>
						<auth>
							<username>${docker.username}</username>
							<password>${docker.password}</password>
						</auth>
					</to>
					<container>
						<entrypoint>/app/entrypoint.sh</entrypoint>
					</container>
					<extraDirectories>
						<paths>
							<path>
								<from>target</from>
								<includes>*.jar</includes>
								<into>/app</into>
							</path>
							<path>
								<from>dockerfiles</from>
								<includes>entrypoint.sh</includes>
								<into>/app</into>
							</path>
							<path>
								<from>target/classes/opencv/linux-amd64</from>
								<includes>*.so</includes>
								<into>/app/opencv</into>
							</path>
						</paths>
						<permissions>
							<permission>
								<file>/app/entrypoint.sh</file>
								<mode>755</mode>
							</permission>
						</permissions>
					</extraDirectories>
				</configuration>
			</plugin>

			<plugin>
				<groupId>org.codehaus.mojo</groupId>
				<artifactId>build-helper-maven-plugin</artifactId>
				<version>3.2.0</version>
				<executions>
					<execution>
						<id>timestamp-property</id>
						<goals>
							<goal>timestamp-property</goal>
						</goals>
						<configuration>
							<name>buildtime</name>
							<pattern>yyyy.MM.dd HH:mm:ss.SSSSSS</pattern>
							<timeZone>Asia/Seoul</timeZone>
						</configuration>
					</execution>
				</executions>
			</plugin>

			<plugin>
				<groupId>com.microsoft.azure</groupId>
				<artifactId>azure-webapp-maven-plugin</artifactId>
				<version>2.2.1</version>
				<configuration>
					<auth>
						<type>service_principal</type>
						<serverId>${azure.servicePrincipal}</serverId>
					</auth>
					<subscriptionId>${azure.subscription}</subscriptionId>
					<resourceGroup>${azure.resourceGroup}</resourceGroup>
					<appName>${azure.appName}</appName>
					<runtime>
						<os>Docker</os>
						<image>${docker.image.prefix}/${docker.image.name}:${docker.tag}</image>
						<serverId>${docker.service-id}</serverId>
						<registryUrl>https://${docker.image.prefix}</registryUrl>
					</runtime>
					<appSettings>
						<!-- 이게 있어야 web app과 docker container 사이에 volumn mount(/home/LogFiles)가 됨 -->
						<property>
							<name>WEBSITES_ENABLE_APP_SERVICE_STORAGE</name>
							<value>true</value>
						</property>
						<property>
							<name>WEBSITES_PORT</name>
							<value>8080</value>
						</property>
						<property>
							<name>BUILDTIME</name>
							<value>${buildtime}</value>
						</property>
						<property>
							<name>SPRING_PROFILES_ACTIVE</name>
							<value>${spring.profiles.active}</value>
						</property>
						<property>
							<name>USER_TIMEZONE</name>
							<value>Asia/Seoul</value>
						</property>
					</appSettings>
				</configuration>
			</plugin>
```

Container Image 빌드 및 Azure Web Service 배포
```bash
./mvnw clean package jib:build azure:deploy
```

## 기타 설정
### Email 설정
1. sendgrid api key 생성
1. pom.xml 설정
1. java 소스

pom.xml
```xml
		<dependency>
			<groupId>com.sendgrid</groupId>
			<artifactId>sendgrid-java</artifactId>
			<version>4.7.4</version>
		</dependency>
```

java 소스
```java
public class MailService {
    @Value("${spring.sendgrid.api-key}")
    private String apiKey;

    public void sendMail(String from, String subject, String to, String content, MailAttachement attachement) throws Exception {
        SendGrid sg = new SendGrid(apiKey);

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");

        Mail mail = new Mail(new Email(from), // from
            subject, // subject
            new Email(to), // to
            new Content("text/html", content));

        if (attachement != null) {
            mail.addAttachments(new Attachments.Builder(
                attachement.getFileName(), attachement.getContent()).build());
        }

        try {
            request.setBody(mail.build());

            Response r = sg.api(request);
            switch (r.getStatusCode()) {
            case 401:
                throw new RuntimeException("authorized fail");
            case 202:
                break;
            default:
                throw new RuntimeException(
                    String.format("send fail: status %d %s", r.getStatusCode(), r.getBody()));
            }
        } catch (Exception e) {
            throw e;
        }
    }
}
```

### SMS 설정
sms는 저작권 상 비공개입니다.