# VisualNovel

These instructions will help you get a copy of the project up and running on your local machine for development and testing purposes. 

## Running Locally

To run the project locally, follow these steps:

1. Clone the repository:
```bash
git clone https://github.com/Sapog87/VisualNovel.git
cd VisualNovel
```

2. Build and start the containers:
```bash
docker-compose up --build
```

## Deploying on a Remote Server

To deploy the project on a remote server, follow these steps:

1. Ensure you have SSH access to the remote server and the correct permissions.
2. Run the deployment script:
### Linux
```bash
./deploy.sh -s username@address -k /path/to/ssh_key
```
### Windows
```pwsh
.\deploy.bat -s username@address -k /path/to/ssh_key
```