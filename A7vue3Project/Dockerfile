# 使用轻量级Nginx镜像
FROM nginx:alpine

# 删除Nginx默认配置（可选，避免冲突）
RUN rm -f /etc/nginx/conf.d/default.conf

# 复制你的nginx.conf到容器的Nginx配置目录
COPY nginx.conf /etc/nginx/conf.d/

# 复制前端打包后的dist目录到Nginx的静态文件目录
COPY dist/ /usr/share/nginx/html/

# 暴露80端口（与nginx.conf中listen的端口一致）
EXPOSE 80

# 启动Nginx（前台运行，确保容器不退出）
CMD ["nginx", "-g", "daemon off;"]