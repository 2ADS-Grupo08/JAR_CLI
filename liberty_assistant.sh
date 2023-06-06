#!/bin/bash
CIANO='\033[0;36m'
NC='\033[0m'
clear
echo -e "${CIANO} _     _ _               _            ____       "
echo -e "| |   (_) |__   ___ _ __| |_ _   _   / ___|___   "
echo -e "| |   | | '_ \ / _ \ '__| __| | | | | |   / _ \  "
echo -e "| |___| | |_) |  __/ |  | |_| |_| | | |__| (_) | "
echo -e "|_____|_|_.__/ \___|_|   \__|\__, |  \____\___/  "
echo -e "                             |___/               ${NC}"


sleep 3
clear

echo -e "${CIANO}[Liberty-assistant]${NC}: Olá, serei seu assistente para instalação do Java, docker e MYSQL!!!"
sleep 3
echo -e "${CIANO}[Liberty-assistant]${NC}: Irei atualizar os pacotes do sistema. Só um momento!"
sudo apt update && sudo apt upgrade –y
echo -e "${CIANO}[Liberty-assistant]${NC}: Pacotes atualizados!"
clear
sleep 2
echo -e "${CIANO}[Liberty-assistant]${NC}: Verificando se você possui o Java instalado..."
echo -e "\n"

sleep 3
java -version >/dev/null 2>&1

if [ $? -eq 0 ]
	then
		echo -e "${CIANO}[Liberty-assistant]${NC}: Você já tem o java instalado!!!"
		echo -e "    "
		sleep 1

		echo -e "${CIANO}[Liberty-assistant]${NC}: E essa é a sua atual versão:"
		java -version
		sleep 3
		clear
	else
		echo -e"${CIANO}[Liberty-assistant]${NC}:  Opa! Não identifiquei nenhuma versão do Java instalado, mas sem problemas, irei resolver isso agora!"
		echo -e "${CIANO}[Liberty-assistant]${NC}:  Confirme para se realmente deseja instalar o Java (S/N)?"
	read inst
		if [ \"$inst\" == \"S\" ]
			then
				echo -e "${CIANO}[Liberty-assistant]${NC}:  Ok! Você escolheu instalar o Java"
				echo -e "${CIANO}[Liberty-assistant]${NC}: Atualizando Java para a versão 17. Confirme a instalação quando solicitado."
				sudo apt install openjdk-17-jdk -y;
				clear
				echo -e "${CIANO}[Liberty-assistant]${NC}: Java atualizado com sucesso!"
				else
				echo -e "${CIANO}[Liberty-assistant]${NC}: A versão atual do Java não atende aos requisitos mínimos. Atualize o Java para a versão 17 ou superior para continuar."
	fi
fi

sleep 2

echo -e "${CIANO}[Liberty-assistant]${NC}: Verificando se você possui o Docker instalado..."
sleep 2
docker --version >/dev/null 2>&1

if [ $? -eq 0 ]
	then
                echo -e "    "
                echo -e "    "
		echo -e "${CIANO}[Liberty-assistant]${NC}: Você já tem o docker instalado!!!"
                echo -e "    "
		sleep 1
		sudo systemctl start docker
		sudo systemctl enable docker
		
		sleep 3
		clear

		echo -e "${CIANO}[Liberty-assistant]${NC}: E essa é a versão atual do docker:"
		docker --version
	else
		echo -e "${CIANO}[Liberty-assistant]${NC}:  Opa! Não identifiquei nenhuma versão do Docker instalado, mas sem problemas, irei resolver isso agora!"
		echo -e "${CIANO}[Liberty-assistant]${NC}:  Confirme para se realmente deseja instalar o Java (S/N)?"
	read inst
		if [ \"$inst\" == \"S\" ]
			then
				echo -e "${CIANO}[Liberty-assistant]${NC}:  Ok! Você escolheu instalar o Docker"
				echo -e "${CIANO}[Liberty-assistant]${NC}:  Realizando download!"
				sleep 2
				sudo apt install docker.io -y				
				echo -e "\n"
				echo -e "${CIANO}[Liberty-assistant]${NC}:  Atualizando! Quase lá."
				sleep 2
				echo -e "\n"
				sudo apt update && sudo apt upgrade –y
		                echo -e "    "
		                echo -e "    "
				echo -e "${CIANO}[Liberty-assistant]${NC}: Obrigado pela paciência! Aproveite nossas ferramentas!"
		fi
fi
echo -e "    "
echo -e "    "
echo -e "${CIANO}[Liberty-assistant]${NC}: Verificando se você possui o MySQL instalado..."
echo -e "    "

sleep 2
sudo docker images | grep mysql >/dev/null 2>&1

if [ $? -eq 0 ]
        then
                echo -e "${CIANO}[Liberty-assistant]${NC}: Você já tem o MySQL instalado!!!"
		echo -e "    "
                echo -e "${CIANO}[Liberty-assistant]${NC}: A versão da imagem SQL é 5.7"
 		sleep 3
 		clear
        else
                echo -e "${CIANO}[Liberty-assistant]${NC}: Opa! Não identifiquei nenhuma versão da imagem do MySQL instalado, mas sem problemas, irei resolver isso agora!"
                echo -e "${CIANO}[Liberty-assistant]${NC}: Confirme para se realmente deseja instalar a imagem MySQL (S/N)?"
        read inst
                if [ \"$inst\" == \"S\" ]
                        then
                                echo -e "${CIANO}[Liberty-assistant]${NC}: Ok! Você escolheu instalar a imagem MySQL"
                                echo -e "${CIANO}[Liberty-assistant]${NC}: Realizando download!"
                                sleep 2
				sudo docker pull mysql:5.7
                                echo -e "\n"
                                echo -e "${CIANO}[Liberty-assistant]${NC}: Atualizando! Quase lá."
                                sleep 2
                                echo -e "\n"
                                sudo apt update && sudo apt upgrade –y
                                echo -e "${CIANO}[Liberty-assistant]${NC}: Obrigado pela paciência! Aproveite nossas ferramentas!"
                                sleep 3
                                clear
                fi
fi
	sleep 3
	echo -e "${CIANO}[Liberty-assistant]${NC}: Agora que fora realizado a instalação de todas as ferramentas, iremos criar o ambiente!"
	echo -e "${CIANO}[Liberty-assistant]${NC}: Vamos criar uma imagem do banco de dados MYSQL."
        echo -e "    "
        echo -e "    "
	sleep 1

if [ $( sudo docker ps -a | grep liberty-co | wc -l ) -gt 0 ];
	then
		echo -e "${CIANO}[Liberty-assistant]${NC}: Você já possuí o banco na sua máquina! Vamos inicializar esse container"
		sleep 2
				sudo docker start liberty-co >/dev/null 2>&1
		sleep 2
		echo -e "${CIANO}[Libert-assistant]${NC}: Inicializado container Liberty!"

	else
		echo -e "${CIANO}[Liberty-assistant]${NC}: Não encontramos nenhum banco do mysql na sua máquina, vamos resolver isso!"
		sleep 2
		sudo docker run -d -p 3306:3306 --name liberty-co -e "MYSQL_DATABASE=liberty-co" -e "MYSQL_ROOT_PASSWORD=#Gfgrupo8" mysql:5.7
		sleep 14
		sudo docker exec -it liberty-co mysql -u root -p#Gfgrupo8 -e "USE liberty-co;
CREATE TABLE  IF NOT EXISTS Instituicao (
	idInstituicao INT PRIMARY KEY AUTO_INCREMENT,
    razao_social VARCHAR(120),
    cnpj VARCHAR(14),
    cep VARCHAR(8),
    numero INT,
    complemento VARCHAR(10),
    email VARCHAR(100),
    senha VARCHAR(45)
);
CREATE TABLE IF NOT EXISTS Gestor (
	idGestor INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(45),
    sobrenome VARCHAR (45),
    cpf varchar(9),
    cargo VARCHAR(25),
    email VARCHAR(45),
    senha VARCHAR(45),
    status varchar(45),
    fkInstituicao INT
);

CREATE TABLE IF NOT EXISTS Telefone (
	idTelefone INT PRIMARY KEY AUTO_INCREMENT,
    numTelefone CHAR(14),
    numCelular CHAR(15),
    fkInstituicao INT,
    fkGestor INT
);
CREATE TABLE IF NOT EXISTS Maquina (
	idMaquina INT PRIMARY KEY AUTO_INCREMENT,
    hostName VARCHAR(45),
    sistemaOperacional VARCHAR(45),
    nomeDono VARCHAR(45),
    sobrenomeDono VARCHAR(45),
    status VARCHAR(45),	
    fkGestor INT
);
CREATE TABLE IF NOT EXISTS Processo (
	idProcesso INT PRIMARY KEY AUTO_INCREMENT,
    nomeProcesso VARCHAR(45),
    fkGestor INT,
    fkMaquina INT
);
CREATE TABLE IF NOT EXISTS Componente (
	idComponente INT PRIMARY KEY AUTO_INCREMENT,
	nomeComponente VARCHAR(45),
	total DECIMAL(6,2),
	modelo VARCHAR(100),
	fkMaquina INT
);
CREATE TABLE IF NOT EXISTS Log (
	idLog INT PRIMARY KEY AUTO_INCREMENT,
	momentoCaptura VARCHAR(100),
	emUso DECIMAL(6,2),
	fkComponente INT,
	fkMaquina INT
);

CREATE TABLE IF NOT EXISTS Janela (
	idJanela INT PRIMARY KEY AUTO_INCREMENT,
	nomeJanela VARCHAR(100),
	fkMaquina INT
);
CREATE TABLE IF NOT EXISTS JanelaEncerrada (
	idJanelaEncerrada INT PRIMARY KEY AUTO_INCREMENT,
	pid VARCHAR(100),
	nomeJanela VARCHAR(100),
	momentoEncerrado DATETIME,
	fkMaquina INT
);"

sleep 20
	fi

        echo -e "    "
        echo -e "    "
echo -e "${CIANO}[Liberty-assistant]${NC}: Agora foram criados os bancos em sua máquina, o monitoramento será iniciado após a instalação de nossa aplicação! Peço que aguarde a instalação!"
        echo -e "    "
        echo -e "    "
sleep 3

if [ -f "captura-componentes-liberty-co-1.0-SNAPSHOT-jar-with-dependencies.jar" ];
	then
		echo -e "${CIANO}[Liberty-assistant]${NC}:Visto que já possuí o arquivo baixado, não iremos baixar novamente"
		sleep 2
	else
		echo -e "${CIANO}[Liberty-assistant]${NC}:Baixado o arquivo, aguarde para execução!"
		echo -e "    "
		echo -e "    "
		sleep 2
fi

if [ "$(sudo docker images -q amazoncorretto:17 2> /dev/null)" ]; 
then
	echo "[Liberty-assistant]:Encontrei a imagem java!"
else
	echo "[Liberty-assistant]: Não encontrei nenhuma imagem para o Container Java. Vamos resolver isso!"
	sudo docker pull amazoncorretto:17
fi


if [ "$(sudo docker ps -aqf name=liberty-co-java)" ];
then
    echo "Verifiquei e vi que você possui o Container JAVA"
	sudo docker start liberty-co-java;
else
    sudo docker run -d --name liberty-co-java amazoncorretto:17 sleep infinity
fi

echo "[Liberty-assistant]: Sua maquina já está preparada, agora vamos baixar o aplicativo da Liberty Company"

        sleep 2
		wget https://github.com/2ADS-Grupo08/JAR_CLI/raw/main/captura-componentes-liberty-co/target/captura-componentes-liberty-co-1.0-SNAPSHOT-jar-with-dependencies.jar

        echo "[Liberty-assistant]: Concluindo Instalação..."
		

        echo "[Liberty-assistant]:Executando aplicação"
        sleep 3

        chmod +x captura-componentes-liberty-co-1.0-SNAPSHOT-jar-with-dependencies.jar
        java -jar captura-componentes-liberty-co-1.0-SNAPSHOT-jar-with-dependencies.jar

        echo "[Liberty-assistant]:executando serviço"
        sleep 3
        exit
            echo "[Liberty-assistant]: Encerrando sistema!"
        sleep 3
        exit


echo -e "[Liberty-assistant]: Obrigado por instalar nossa solução, peço que não encerre a aba de monitoramento! A Liberty Company agradece.
