FROM nginx:stable-alpine
COPY dist/ /usr/share/nginx/html/
COPY nginx.conf /etc/nginx/nginx.conf
USER 101:101
EXPOSE 8082
ENTRYPOINT ["nginx"]
CMD ["-g", "daemon off;"]
