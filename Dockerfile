FROM minio/mc:latest

ENV MINIO_ACCESS_KEY=rootuser
ENV MINIO_SECRET_KEY=rootpassword
ENV MINIO_SERVER=http://minio:9000

ENTRYPOINT ["mc", "alias", "set", "myminio", "$MINIO_SERVER", "$MINIO_ACCESS_KEY", "$MINIO_SECRET_KEY"]
CMD ["mc", "mb", "myminio/bucket", "--ignore-existing"]
