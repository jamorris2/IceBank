FROM ubuntu:latest
LABEL authors="jason"

ENTRYPOINT ["top", "-b"]