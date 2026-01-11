#!/bin/bash
# ========================================
# Script de déploiement Cinema Management
# ========================================

set -e  # Arrêter en cas d'erreur

# Configuration
WAR_FILE="target/cinema-management.war"
TOMCAT_WEBAPPS="/opt/tomcat/webapps"
APP_NAME="cinema-management"
BACKUP_DIR="/opt/tomcat/backups"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# Couleurs pour l'affichage
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "========================================="
echo "  Déploiement Cinema Management"
echo "========================================="

# 1. Vérifier que le WAR existe
if [ ! -f "$WAR_FILE" ]; then
    echo -e "${RED}❌ Erreur: Le fichier $WAR_FILE n'existe pas!${NC}"
    echo "Exécutez d'abord: mvn clean package"
    exit 1
fi

echo -e "${GREEN}✓${NC} WAR trouvé: $WAR_FILE"

# 2. Vérifier que Tomcat est accessible
if [ ! -d "$TOMCAT_WEBAPPS" ]; then
    echo -e "${RED}❌ Erreur: Répertoire Tomcat $TOMCAT_WEBAPPS introuvable!${NC}"
    exit 1
fi

echo -e "${GREEN}✓${NC} Répertoire Tomcat accessible"

# 3. Créer le répertoire de backup si nécessaire
mkdir -p "$BACKUP_DIR"

# 4. Sauvegarder l'ancienne version si elle existe
if [ -f "$TOMCAT_WEBAPPS/${APP_NAME}.war" ]; then
    echo -e "${YELLOW}→${NC} Sauvegarde de l'ancienne version..."
    cp "$TOMCAT_WEBAPPS/${APP_NAME}.war" "$BACKUP_DIR/${APP_NAME}_${TIMESTAMP}.war"
    echo -e "${GREEN}✓${NC} Sauvegarde créée: $BACKUP_DIR/${APP_NAME}_${TIMESTAMP}.war"
fi

# 5. Supprimer l'ancienne application déployée
if [ -d "$TOMCAT_WEBAPPS/$APP_NAME" ]; then
    echo -e "${YELLOW}→${NC} Suppression de l'ancien déploiement..."
    rm -rf "$TOMCAT_WEBAPPS/$APP_NAME"
fi

if [ -f "$TOMCAT_WEBAPPS/${APP_NAME}.war" ]; then
    rm -f "$TOMCAT_WEBAPPS/${APP_NAME}.war"
fi

# 6. Copier le nouveau WAR
echo -e "${YELLOW}→${NC} Déploiement du nouveau WAR..."
cp "$WAR_FILE" "$TOMCAT_WEBAPPS/"

# 7. Ajuster les permissions si nécessaire
if [ -n "$TOMCAT_USER" ]; then
    chown $TOMCAT_USER:$TOMCAT_USER "$TOMCAT_WEBAPPS/${APP_NAME}.war"
    echo -e "${GREEN}✓${NC} Permissions ajustées pour l'utilisateur: $TOMCAT_USER"
fi

echo -e "${GREEN}✓${NC} WAR déployé avec succès!"

# 8. Redémarrer Tomcat (optionnel - commenter si non souhaité)
echo -e "${YELLOW}→${NC} Redémarrage de Tomcat..."

# Détecter la méthode de gestion de Tomcat
if systemctl is-active --quiet tomcat 2>/dev/null; then
    # Tomcat géré par systemd
    echo "  (via systemctl)"
    sudo systemctl restart tomcat
    echo -e "${GREEN}✓${NC} Tomcat redémarré (systemctl)"
elif [ -f "/opt/tomcat/bin/shutdown.sh" ] && [ -f "/opt/tomcat/bin/startup.sh" ]; then
    # Tomcat standalone
    echo "  (via scripts catalina)"
    /opt/tomcat/bin/shutdown.sh 2>/dev/null || true
    sleep 3
    /opt/tomcat/bin/startup.sh
    echo -e "${GREEN}✓${NC} Tomcat redémarré (scripts)"
else
    echo -e "${YELLOW}⚠${NC}  Tomcat non redémarré automatiquement"
    echo "   Redémarrez manuellement avec: sudo systemctl restart tomcat"
fi

# 9. Attendre le déploiement
echo -e "${YELLOW}→${NC} Attente du déploiement (10 secondes)..."
sleep 10

# 10. Vérifier que l'application est déployée
if [ -d "$TOMCAT_WEBAPPS/$APP_NAME" ]; then
    echo -e "${GREEN}✓${NC} Application déployée avec succès!"
    echo ""
    echo "========================================="
    echo "  URL de l'application:"
    echo "  http://localhost:8080/${APP_NAME}/"
    echo "========================================="
else
    echo -e "${YELLOW}⚠${NC}  Le répertoire de déploiement n'existe pas encore"
    echo "   Vérifiez les logs Tomcat: tail -f /opt/tomcat/logs/catalina.out"
fi

# 11. Nettoyer les anciennes sauvegardes (garder les 5 dernières)
echo -e "${YELLOW}→${NC} Nettoyage des anciennes sauvegardes..."
cd "$BACKUP_DIR"
ls -t ${APP_NAME}_*.war 2>/dev/null | tail -n +6 | xargs -r rm
echo -e "${GREEN}✓${NC} Anciennes sauvegardes nettoyées"

echo ""
echo -e "${GREEN}=========================================${NC}"
echo -e "${GREEN}  Déploiement terminé avec succès! ✓${NC}"
echo -e "${GREEN}=========================================${NC}"
