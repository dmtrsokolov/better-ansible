
`docker run -d --name ssh.pool-1.1.1 -p 2020:22 --env='SSH_SUDO="ALL=(ALL) NOPASSWD:ALL"' jdeathe/centos-ssh:centos-7`

`ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -p 2020 -i id_rsa_insecure app-admin@localhost`