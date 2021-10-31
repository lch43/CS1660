# Help from https://typeofnan.dev/how-to-serve-a-react-app-with-nginx-in-docker/

FROM node:10 AS app_builder
WORKDIR /app
COPY . .
RUN yarn install && yarn build

FROM nginx:alpine
WORKDIR /usr/share/nginx/html
RUN rm -rf ./*
COPY --from=app_builder /app/build .
ENTRYPOINT ["nginx", "-g", "daemon off;"]