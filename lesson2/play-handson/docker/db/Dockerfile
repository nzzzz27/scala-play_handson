# mysqlは5.7を使用
FROM mysql:5.7
RUN mkdir /var/log/mysql_data
RUN touch /var/log/mysql_data/mysqld.log

# imageがdebianのため、apt-getで日本のlocaleを追加
RUN apt-get update && \
    apt-get install -y locales && \
    rm -rf /var/lib/apt/lists/* && \
    echo "ja_JP.UTF-8 UTF-8" > /etc/locale.gen && \
    locale-gen ja_JP.UTF-8
ENV LC_ALL ja_JP.UTF-8

# docker/db/my.cnfをdockerイメージ上の/etc/...にコピーして配置
COPY ./my.cnf /etc/mysql/conf.d/my.cnf

# yamlのservicesにdbを追加したら以下のエラーあり。mysqlid.logファイルを作成したらエラーなくなった
# Step 6/6 : COPY ./my.cnf /etc/mysql/conf.d/my.cnf
# ERROR: Service 'db' failed to build: COPY failed: stat /var/lib/docker/tmp/docker-builder484160919/my.cnf: no such file or directory 
