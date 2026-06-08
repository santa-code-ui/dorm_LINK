#!/bin/bash
set -e
echo "=== dorm_LINK Codespace Setup ==="

# Install Maven
sudo apt-get update -q && sudo apt-get install -y maven > /dev/null

# Download Tomcat 10
TOMCAT_VERSION="10.1.31"
TOMCAT_DIR="$HOME/tomcat10"
if [ ! -d "$TOMCAT_DIR" ]; then
  echo "Downloading Tomcat $TOMCAT_VERSION..."
  curl -sL "https://dlcdn.apache.org/tomcat/tomcat-10/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz" \
    | tar -xz -C $HOME/
  mv $HOME/apache-tomcat-${TOMCAT_VERSION} $TOMCAT_DIR
  chmod +x $TOMCAT_DIR/bin/*.sh
fi
echo "export CATALINA_HOME=$TOMCAT_DIR" >> ~/.bashrc

# Wait for MySQL
echo "Waiting for MySQL..."
for i in {1..20}; do
  mysql -uroot -proot -e "SELECT 1;" &>/dev/null && break || sleep 3
done

# Import database
echo "Importing schema and seed data..."
mysql -uroot -proot < /workspaces/dorm_LINK/database/schema.sql
mysql -uroot -proot < /workspaces/dorm_LINK/database/seed_data.sql
echo "Database ready."

# Build Maven project
WORK=/workspaces/dorm_LINK
mkdir -p $WORK/dormlink/src/main/java/{config,controllers,dao,models,services,utils}
mkdir -p $WORK/dormlink/src/main/resources
mkdir -p $WORK/dormlink/src/main/webapp/WEB-INF
mkdir -p $WORK/dormlink/src/main/webapp/frontend/{css,js}
mkdir -p $WORK/dormlink/src/main/webapp/frontend/html/{admin,auth,student}
mkdir -p $WORK/dormlink/src/main/webapp/frontend/assets/{images,logos,qr,icons}
mkdir -p $WORK/dormlink/src/main/webapp/uploads/{hostel_images,receipts,student_photos}

cp $WORK/backend/config/*.java      $WORK/dormlink/src/main/java/config/
cp $WORK/backend/controllers/*.java $WORK/dormlink/src/main/java/controllers/
cp $WORK/backend/dao/*.java         $WORK/dormlink/src/main/java/dao/
cp $WORK/backend/models/*.java      $WORK/dormlink/src/main/java/models/
cp $WORK/backend/services/*.java    $WORK/dormlink/src/main/java/services/
cp $WORK/backend/utils/*.java       $WORK/dormlink/src/main/java/utils/
cp $WORK/backend/config/db.properties $WORK/dormlink/src/main/resources/
sed -i 's/^db.password=.*/db.password=root/' $WORK/dormlink/src/main/resources/db.properties

cp $WORK/frontend/css/*.css              $WORK/dormlink/src/main/webapp/frontend/css/
cp $WORK/frontend/js/*.js               $WORK/dormlink/src/main/webapp/frontend/js/
cp $WORK/frontend/html/admin/*.html     $WORK/dormlink/src/main/webapp/frontend/html/admin/
cp $WORK/frontend/html/auth/*.html      $WORK/dormlink/src/main/webapp/frontend/html/auth/
cp $WORK/frontend/html/student/*.html   $WORK/dormlink/src/main/webapp/frontend/html/student/
cp $WORK/frontend/assets/images/* $WORK/dormlink/src/main/webapp/frontend/assets/images/ 2>/dev/null || true
cp $WORK/frontend/assets/logos/*  $WORK/dormlink/src/main/webapp/frontend/assets/logos/  2>/dev/null || true

cp $WORK/.devcontainer/pom.xml    $WORK/dormlink/pom.xml
cp $WORK/.devcontainer/web.xml    $WORK/dormlink/src/main/webapp/WEB-INF/web.xml
cp $WORK/.devcontainer/index.html $WORK/dormlink/src/main/webapp/index.html

echo "Building WAR..."
cd $WORK/dormlink && mvn clean package -q
cp $WORK/dormlink/target/dormlink.war $TOMCAT_DIR/webapps/

echo "Starting Tomcat on port 8080..."
$TOMCAT_DIR/bin/startup.sh

echo ""
echo "✓ Done! dorm_LINK is live."
echo "  Admin:   admin / admin123"
echo "  Student: USTM2024CS001 / student123"
