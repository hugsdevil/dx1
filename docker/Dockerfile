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