package SmartAgricultural.Management.Service;

import SmartAgricultural.Management.Model.Transaction;
import SmartAgricultural.Management.dto.UserDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    // ==================== BASIC EMAIL METHODS ====================

    /**
     * Sends a plain text email
     */
    public void sendEmail(String to, String subject, String body) {
        try {
            logger.info("Preparing to send email to: {}", to);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false);

            logger.info("Sending email to: {}", to);
            mailSender.send(message);
            logger.info("Email sent successfully to: {}", to);
        } catch (MessagingException e) {
            logger.error("Failed to send email to: " + to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Sends an HTML email
     */
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            logger.info("Preparing to send HTML email to: {}", to);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            logger.info("Sending HTML email to: {}", to);
            mailSender.send(message);
            logger.info("HTML email sent successfully to: {}", to);
        } catch (MessagingException e) {
            logger.error("Failed to send HTML email to: " + to, e);
            throw new RuntimeException("Failed to send HTML email", e);
        }
    }

    /**
     * Sends an email with CC recipients
     */
    public void sendEmailWithCC(String to, String[] ccRecipients, String subject, String body) {
        try {
            logger.info("Preparing to send email to: {} with {} CC recipients", to, ccRecipients != null ? ccRecipients.length : 0);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false);

            if (ccRecipients != null && ccRecipients.length > 0) {
                String[] validCcRecipients = java.util.Arrays.stream(ccRecipients)
                        .filter(cc -> cc != null && !cc.trim().isEmpty())
                        .toArray(String[]::new);

                if (validCcRecipients.length > 0) {
                    helper.setCc(validCcRecipients);
                    logger.info("Added {} CC recipients", validCcRecipients.length);
                }
            }

            mailSender.send(message);
            logger.info("Email sent successfully to: {} with CC recipients", to);
        } catch (MessagingException e) {
            logger.error("Failed to send email with CC to: " + to, e);
            throw new RuntimeException("Failed to send email with CC", e);
        }
    }

    // ==================== WELCOME EMAIL - BILINGUAL ====================

    /**
     * Sends a professional bilingual welcome email to new users
     */
    public void sendBilingualWelcomeEmail(UserDTO user) {
        String subject = "🌱 Welcome to Smart Agriculture | Bienvenue sur Smart Agriculture";
        String htmlBody = createBilingualWelcomeEmailTemplate(user);
        sendHtmlEmail(user.getEmail(), subject, htmlBody);
        logger.info("Bilingual welcome email sent to: {} ({})", user.getEmail(), user.getRole());
    }

    private String createBilingualWelcomeEmailTemplate(UserDTO user) {
        String roleName = getRoleDisplayName(user.getRole());
        String roleNameFr = getRoleDisplayNameFrench(user.getRole());

        return "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "    <title>Welcome | Bienvenue</title>" +
                "</head>" +
                "<body style='margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;'>" +
                "    <table role='presentation' style='width: 100%; border-collapse: collapse;'>" +
                "        <tr>" +
                "            <td style='padding: 40px 0;'>" +
                "                <table role='presentation' style='width: 600px; margin: 0 auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1);'>" +
                "                    <!-- Header -->" +
                "                    <tr>" +
                "                        <td style='background: linear-gradient(135deg, #2ecc71 0%, #27ae60 100%); padding: 40px; text-align: center;'>" +
                "                            <h1 style='color: white; margin: 0; font-size: 32px;'>🌱 Smart Agriculture</h1>" +
                "                            <p style='color: #e8f5e9; margin: 10px 0 0 0; font-size: 16px;'>Management System | Système de Gestion</p>" +
                "                        </td>" +
                "                    </tr>" +
                "                    <!-- English Section -->" +
                "                    <tr>" +
                "                        <td style='padding: 40px;'>" +
                "                            <h2 style='color: #27ae60; margin: 0 0 20px 0; font-size: 24px; border-bottom: 3px solid #2ecc71; padding-bottom: 10px;'>Welcome Aboard!</h2>" +
                "                            <p style='color: #333; font-size: 16px; line-height: 1.6; margin: 0 0 15px 0;'>Dear <strong>" + user.getFullName() + "</strong>,</p>" +
                "                            <p style='color: #555; font-size: 15px; line-height: 1.6; margin: 0 0 20px 0;'>We are thrilled to welcome you to the <strong>Smart Agriculture Management System</strong>! Your account has been successfully created.</p>" +
                "                            <table style='width: 100%; background: #e8f5e9; border-left: 4px solid #2ecc71; border-radius: 5px; margin: 20px 0;'>" +
                "                                <tr><td style='padding: 20px;'>" +
                "                                    <p style='margin: 5px 0; color: #333;'><strong style='color: #27ae60;'>Username:</strong> " + user.getUsername() + "</p>" +
                "                                    <p style='margin: 5px 0; color: #333;'><strong style='color: #27ae60;'>Email:</strong> " + user.getEmail() + "</p>" +
                "                                    <p style='margin: 5px 0; color: #333;'><strong style='color: #27ae60;'>Role:</strong> " + roleName + "</p>" +
                "                                    <p style='margin: 5px 0; color: #333;'><strong style='color: #27ae60;'>Phone:</strong> " + user.getPhoneNumber() + "</p>" +
                "                                </td></tr>" +
                "                            </table>" +
                "                            <p style='color: #333; font-size: 16px; margin: 20px 0 10px 0;'><strong>What you can do now:</strong></p>" +
                "                            <table style='width: 100%;'>" +
                "                                <tr><td style='padding: 5px 0;'><span style='color: #2ecc71; font-size: 18px;'>✅</span> <span style='color: #555;'>Log in to your account using your credentials</span></td></tr>" +
                "                                <tr><td style='padding: 5px 0;'><span style='color: #2ecc71; font-size: 18px;'>✅</span> <span style='color: #555;'>Complete your profile information</span></td></tr>" +
                "                                <tr><td style='padding: 5px 0;'><span style='color: #2ecc71; font-size: 18px;'>✅</span> <span style='color: #555;'>Explore all features available to your role</span></td></tr>" +
                "                                <tr><td style='padding: 5px 0;'><span style='color: #2ecc71; font-size: 18px;'>✅</span> <span style='color: #555;'>Connect with the agricultural community</span></td></tr>" +
                "                            </table>" +
                "                        </td>" +
                "                    </tr>" +
                "                    <!-- Divider -->" +
                "                    <tr>" +
                "                        <td style='padding: 0 40px;'>" +
                "                            <div style='height: 2px; background: linear-gradient(to right, transparent, #2ecc71, transparent);'></div>" +
                "                        </td>" +
                "                    </tr>" +
                "                    <!-- French Section -->" +
                "                    <tr>" +
                "                        <td style='padding: 40px;'>" +
                "                            <h2 style='color: #27ae60; margin: 0 0 20px 0; font-size: 24px; border-bottom: 3px solid #2ecc71; padding-bottom: 10px;'>Bienvenue Parmi Nous !</h2>" +
                "                            <p style='color: #333; font-size: 16px; line-height: 1.6; margin: 0 0 15px 0;'>Cher(ère) <strong>" + user.getFullName() + "</strong>,</p>" +
                "                            <p style='color: #555; font-size: 15px; line-height: 1.6; margin: 0 0 20px 0;'>Nous sommes ravis de vous accueillir sur le <strong>Système de Gestion Agricole Intelligente</strong> ! Votre compte a été créé avec succès.</p>" +
                "                            <table style='width: 100%; background: #e8f5e9; border-left: 4px solid #2ecc71; border-radius: 5px; margin: 20px 0;'>" +
                "                                <tr><td style='padding: 20px;'>" +
                "                                    <p style='margin: 5px 0; color: #333;'><strong style='color: #27ae60;'>Nom d'utilisateur:</strong> " + user.getUsername() + "</p>" +
                "                                    <p style='margin: 5px 0; color: #333;'><strong style='color: #27ae60;'>Email:</strong> " + user.getEmail() + "</p>" +
                "                                    <p style='margin: 5px 0; color: #333;'><strong style='color: #27ae60;'>Rôle:</strong> " + roleNameFr + "</p>" +
                "                                    <p style='margin: 5px 0; color: #333;'><strong style='color: #27ae60;'>Téléphone:</strong> " + user.getPhoneNumber() + "</p>" +
                "                                </td></tr>" +
                "                            </table>" +
                "                            <p style='color: #333; font-size: 16px; margin: 20px 0 10px 0;'><strong>Ce que vous pouvez faire maintenant :</strong></p>" +
                "                            <table style='width: 100%;'>" +
                "                                <tr><td style='padding: 5px 0;'><span style='color: #2ecc71; font-size: 18px;'>✅</span> <span style='color: #555;'>Connectez-vous avec vos identifiants</span></td></tr>" +
                "                                <tr><td style='padding: 5px 0;'><span style='color: #2ecc71; font-size: 18px;'>✅</span> <span style='color: #555;'>Complétez vos informations de profil</span></td></tr>" +
                "                                <tr><td style='padding: 5px 0;'><span style='color: #2ecc71; font-size: 18px;'>✅</span> <span style='color: #555;'>Explorez toutes les fonctionnalités</span></td></tr>" +
                "                                <tr><td style='padding: 5px 0;'><span style='color: #2ecc71; font-size: 18px;'>✅</span> <span style='color: #555;'>Rejoignez la communauté agricole</span></td></tr>" +
                "                            </table>" +
                "                        </td>" +
                "                    </tr>" +
                "                    <!-- CTA Button -->" +
                "                    <tr>" +
                "                        <td style='padding: 30px 40px; text-align: center; background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);'>" +
                "                            <h3 style='color: #27ae60; margin: 0 0 15px 0;'>Get Started | Commencez</h3>" +
                "                            <a href='http://localhost:1010/login' style='display: inline-block; padding: 15px 40px; background: #2ecc71; color: white; text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 16px;'>Access Account | Accéder</a>" +
                "                        </td>" +
                "                    </tr>" +
                "                    <!-- Footer -->" +
                "                    <tr>" +
                "                        <td style='padding: 30px 40px; text-align: center; background: #f9f9f9; border-top: 1px solid #e0e0e0;'>" +
                "                            <p style='margin: 0 0 10px 0; color: #333; font-weight: bold;'>Smart Agriculture Management System</p>" +
                "                            <p style='margin: 0 0 10px 0; color: #666; font-size: 14px;'>Empowering Agricultural Excellence | Promouvoir l'Excellence Agricole</p>" +
                "                            <p style='margin: 0 0 15px 0; color: #999; font-size: 12px;'>© 2024 Smart Agriculture. All rights reserved. | Tous droits réservés.</p>" +
                "                            <p style='margin: 0; color: #999; font-size: 11px;'>This is an automated message. Please do not reply.<br>Ceci est un message automatique. Veuillez ne pas répondre.</p>" +
                "                        </td>" +
                "                    </tr>" +
                "                </table>" +
                "            </td>" +
                "        </tr>" +
                "    </table>" +
                "</body>" +
                "</html>";
    }

    // ==================== OTP EMAIL - BILINGUAL ====================

    /**
     * Sends OTP verification email
     */
    public void sendOtpEmail(UserDTO user, String otp) {
        String subject = "🔐 Login Verification Code | Code de Vérification";
        String htmlBody = createOtpEmailTemplate(user, otp);
        sendHtmlEmail(user.getEmail(), subject, htmlBody);
        logger.info("OTP email sent to: {}", user.getEmail());
    }

    private String createOtpEmailTemplate(UserDTO user, String otp) {
        return "<!DOCTYPE html>" +
                "<html><body style='margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;'>" +
                "    <table role='presentation' style='width: 100%; border-collapse: collapse;'><tr><td style='padding: 40px 0;'>" +
                "        <table role='presentation' style='width: 600px; margin: 0 auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1);'>" +
                "            <tr><td style='background: linear-gradient(135deg, #3498db 0%, #2980b9 100%); padding: 40px; text-align: center;'>" +
                "                <h1 style='color: white; margin: 0; font-size: 28px;'>🔐 Security Verification</h1>" +
                "                <p style='color: #e3f2fd; margin: 10px 0 0 0;'>Vérification de Sécurité</p>" +
                "            </td></tr>" +
                "            <tr><td style='padding: 40px;'>" +
                "                <h2 style='color: #2980b9; margin: 0 0 20px 0;'>Login Verification Code</h2>" +
                "                <p style='color: #333; font-size: 16px;'>Dear <strong>" + user.getFullName() + "</strong>,</p>" +
                "                <p style='color: #555; font-size: 15px;'>Your verification code is:</p>" +
                "                <div style='background: #e3f2fd; border: 2px dashed #3498db; padding: 30px; text-align: center; border-radius: 10px; margin: 25px 0;'>" +
                "                    <h1 style='color: #2980b9; margin: 0; font-size: 48px; letter-spacing: 10px;'>" + otp + "</h1>" +
                "                </div>" +
                "                <p style='color: #e74c3c; font-size: 14px;'><strong>⏰ This code expires in 5 minutes.</strong></p>" +
                "                <div style='height: 2px; background: linear-gradient(to right, transparent, #3498db, transparent); margin: 30px 0;'></div>" +
                "                <h2 style='color: #2980b9; margin: 0 0 20px 0;'>Code de Vérification de Connexion</h2>" +
                "                <p style='color: #333; font-size: 16px;'>Cher(ère) <strong>" + user.getFullName() + "</strong>,</p>" +
                "                <p style='color: #555; font-size: 15px;'>Votre code de vérification est affiché ci-dessus.</p>" +
                "                <p style='color: #e74c3c; font-size: 14px;'><strong>⏰ Ce code expire dans 5 minutes.</strong></p>" +
                "                <p style='color: #999; font-size: 13px; margin-top: 20px;'>If you didn't attempt to log in, ignore this email. | Si vous n'avez pas tenté de vous connecter, ignorez cet email.</p>" +
                "            </td></tr>" +
                "            <tr><td style='padding: 30px 40px; text-align: center; background: #f9f9f9; border-top: 1px solid #e0e0e0;'>" +
                "                <p style='margin: 0; color: #333; font-weight: bold;'>Smart Agriculture Security Team</p>" +
                "                <p style='margin: 5px 0 0 0; color: #999; font-size: 12px;'>© 2024 Smart Agriculture. All rights reserved.</p>" +
                "            </td></tr>" +
                "        </table>" +
                "    </td></tr></table>" +
                "</body></html>";
    }

    // ==================== PASSWORD RESET EMAIL - BILINGUAL ====================

    /**
     * Sends password reset email
     */
    public void sendPasswordResetEmail(UserDTO user, String resetToken) {
        String subject = "🔑 Password Reset Request | Réinitialisation du Mot de Passe";
        String htmlBody = createPasswordResetEmailTemplate(user, resetToken);
        sendHtmlEmail(user.getEmail(), subject, htmlBody);
        logger.info("Password reset email sent to: {}", user.getEmail());
    }

    private String createPasswordResetEmailTemplate(UserDTO user, String resetToken) {
        return "<!DOCTYPE html>" +
                "<html><body style='margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;'>" +
                "    <table role='presentation' style='width: 100%; border-collapse: collapse;'><tr><td style='padding: 40px 0;'>" +
                "        <table role='presentation' style='width: 600px; margin: 0 auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1);'>" +
                "            <tr><td style='background: linear-gradient(135deg, #e67e22 0%, #d35400 100%); padding: 40px; text-align: center;'>" +
                "                <h1 style='color: white; margin: 0; font-size: 28px;'>🔑 Password Reset</h1>" +
                "                <p style='color: #fef5e7; margin: 10px 0 0 0;'>Réinitialisation du Mot de Passe</p>" +
                "            </td></tr>" +
                "            <tr><td style='padding: 40px;'>" +
                "                <h2 style='color: #d35400; margin: 0 0 20px 0;'>Password Reset Request</h2>" +
                "                <p style='color: #333; font-size: 16px;'>Dear <strong>" + user.getFullName() + "</strong>,</p>" +
                "                <p style='color: #555; font-size: 15px;'>You requested to reset your password for your <strong>" + getRoleDisplayName(user.getRole()) + "</strong> account.</p>" +
                "                <p style='color: #555; font-size: 15px;'>Use the following code:</p>" +
                "                <div style='background: #fef5e7; border: 2px solid #e67e22; padding: 25px; text-align: center; border-radius: 10px; margin: 25px 0;'>" +
                "                    <h1 style='color: #d35400; margin: 0; font-size: 42px; letter-spacing: 8px;'>" + resetToken + "</h1>" +
                "                </div>" +
                "                <p style='color: #e74c3c; font-size: 14px;'><strong>⏰ This code expires in 1 hour.</strong></p>" +
                "                <div style='height: 2px; background: linear-gradient(to right, transparent, #e67e22, transparent); margin: 30px 0;'></div>" +
                "                <h2 style='color: #d35400; margin: 0 0 20px 0;'>Demande de Réinitialisation</h2>" +
                "                <p style='color: #333; font-size: 16px;'>Cher(ère) <strong>" + user.getFullName() + "</strong>,</p>" +
                "                <p style='color: #555; font-size: 15px;'>Vous avez demandé la réinitialisation de votre mot de passe pour votre compte <strong>" + getRoleDisplayNameFrench(user.getRole()) + "</strong>.</p>" +
                "                <p style='color: #555; font-size: 15px;'>Utilisez le code affiché ci-dessus.</p>" +
                "                <p style='color: #e74c3c; font-size: 14px;'><strong>⏰ Ce code expire dans 1 heure.</strong></p>" +
                "                <div style='background: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0; border-radius: 5px;'>" +
                "                    <p style='margin: 0; color: #856404; font-size: 14px;'><strong>⚠️ Security Note:</strong> If you didn't request this, ignore this email. | Si vous n'avez pas fait cette demande, ignorez cet email.</p>" +
                "                </div>" +
                "            </td></tr>" +
                "            <tr><td style='padding: 30px 40px; text-align: center; background: #f9f9f9; border-top: 1px solid #e0e0e0;'>" +
                "                <p style='margin: 0; color: #333; font-weight: bold;'>Smart Agriculture Security Team</p>" +
                "                <p style='margin: 5px 0 0 0; color: #999; font-size: 12px;'>© 2024 Smart Agriculture. All rights reserved.</p>" +
                "            </td></tr>" +
                "        </table>" +
                "    </td></tr></table>" +
                "</body></html>";
    }

    // ==================== ACCOUNT ACTIVATION EMAIL ====================

    /**
     * Sends account activation notification
     */
    public void sendAccountActivationEmail(UserDTO user) {
        String subject = "✅ Account Activated | Compte Activé";
        String htmlBody = createAccountActivationEmailTemplate(user);
        sendHtmlEmail(user.getEmail(), subject, htmlBody);
        logger.info("Account activation email sent to: {}", user.getEmail());
    }

    private String createAccountActivationEmailTemplate(UserDTO user) {
        return "<!DOCTYPE html>" +
                "<html><body style='margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;'>" +
                "    <table role='presentation' style='width: 100%; border-collapse: collapse;'><tr><td style='padding: 40px 0;'>" +
                "        <table role='presentation' style='width: 600px; margin: 0 auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1);'>" +
                "            <tr><td style='background: linear-gradient(135deg, #2ecc71 0%, #27ae60 100%); padding: 40px; text-align: center;'>" +
                "                <h1 style='color: white; margin: 0; font-size: 28px;'>✅ Account Activated</h1>" +
                "                <p style='color: #e8f5e9; margin: 10px 0 0 0;'>Compte Activé</p>" +
                "            </td></tr>" +
                "            <tr><td style='padding: 40px;'>" +
                "                <h2 style='color: #27ae60; margin: 0 0 20px 0;'>Great News!</h2>" +
                "                <p style='color: #333; font-size: 16px;'>Dear <strong>" + user.getFullName() + "</strong>,</p>" +
                "                <p style='color: #555; font-size: 15px;'>Your Smart Agriculture account has been successfully activated!</p>" +
                "                <div style='background: #e8f5e9; border-left: 4px solid #2ecc71; padding: 20px; margin: 20px 0; border-radius: 5px;'>" +
                "                    <p style='margin: 5px 0; color: #333;'><strong>Username:</strong> " + user.getUsername() + "</p>" +
                "                    <p style='margin: 5px 0; color: #333;'><strong>Email:</strong> " + user.getEmail() + "</p>" +
                "                    <p style='margin: 5px 0; color: #333;'><strong>Role:</strong> " + getRoleDisplayName(user.getRole()) + "</p>" +
                "                </div>" +
                "                <div style='height: 2px; background: linear-gradient(to right, transparent, #2ecc71, transparent); margin: 30px 0;'></div>" +
                "                <h2 style='color: #27ae60; margin: 0 0 20px 0;'>Excellente Nouvelle !</h2>" +
                "                <p style='color: #333; font-size: 16px;'>Cher(ère) <strong>" + user.getFullName() + "</strong>,</p>" +
                "                <p style='color: #555; font-size: 15px;'>Votre compte Smart Agriculture a été activé avec succès !</p>" +
                "                <div style='text-align: center; margin: 30px 0;'>" +
                "                    <a href='http://localhost:1010/login' style='display: inline-block; padding: 15px 40px; background: #2ecc71; color: white; text-decoration: none; border-radius: 5px; font-weight: bold;'>Login Now | Se Connecter</a>" +
                "                </div>" +
                "            </td></tr>" +
                "            <tr><td style='padding: 30px 40px; text-align: center; background: #f9f9f9; border-top: 1px solid #e0e0e0;'>" +
                "                <p style='margin: 0; color: #333; font-weight: bold;'>Smart Agriculture Team</p>" +
                "                <p style='margin: 5px 0 0 0; color: #999; font-size: 12px;'>© 2024 Smart Agriculture. All rights reserved.</p>" +
                "            </td></tr>" +
                "        </table>" +
                "    </td></tr></table>" +
                "</body></html>";
    }

    // ==================== ACCOUNT DEACTIVATION EMAIL ====================

    /**
     * Sends account deactivation notification
     */
    public void sendAccountDeactivationEmail(UserDTO user) {
        String subject = "⚠️ Account Deactivated | Compte Désactivé";
        String htmlBody = createAccountDeactivationEmailTemplate(user);
        sendHtmlEmail(user.getEmail(), subject, htmlBody);
        logger.info("Account deactivation email sent to: {}", user.getEmail());
    }

    private String createAccountDeactivationEmailTemplate(UserDTO user) {
        return "<!DOCTYPE html>" +
                "<html><body style='margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;'>" +
                "    <table role='presentation' style='width: 100%; border-collapse: collapse;'><tr><td style='padding: 40px 0;'>" +
                "        <table role='presentation' style='width: 600px; margin: 0 auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1);'>" +
                "            <tr><td style='background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%); padding: 40px; text-align: center;'>" +
                "                <h1 style='color: white; margin: 0; font-size: 28px;'>⚠️ Account Deactivated</h1>" +
                "                <p style='color: #fadbd8; margin: 10px 0 0 0;'>Compte Désactivé</p>" +
                "            </td></tr>" +
                "            <tr><td style='padding: 40px;'>" +
                "                <h2 style='color: #c0392b; margin: 0 0 20px 0;'>Account Status Update</h2>" +
                "                <p style='color: #333; font-size: 16px;'>Dear <strong>" + user.getFullName() + "</strong>,</p>" +
                "                <p style='color: #555; font-size: 15px;'>We're writing to inform you that your Smart Agriculture Management System account has been deactivated.</p>" +
                "                <div style='background: #fadbd8; border-left: 4px solid #e74c3c; padding: 20px; margin: 20px 0; border-radius: 5px;'>" +
                "                    <p style='margin: 0; color: #c0392b;'><strong>⚠️ Note:</strong> You will not be able to access your account until it is reactivated.</p>" +
                "                </div>" +
                "                <p style='color: #555; font-size: 15px;'>If you believe this was done in error or would like to reactivate your account, please contact our support team.</p>" +
                "                <div style='height: 2px; background: linear-gradient(to right, transparent, #e74c3c, transparent); margin: 30px 0;'></div>" +
                "                <h2 style='color: #c0392b; margin: 0 0 20px 0;'>Mise à Jour du Statut du Compte</h2>" +
                "                <p style='color: #333; font-size: 16px;'>Cher(ère) <strong>" + user.getFullName() + "</strong>,</p>" +
                "                <p style='color: #555; font-size: 15px;'>Nous vous informons que votre compte sur le Système de Gestion Agricole Intelligente a été désactivé.</p>" +
                "                <p style='color: #555; font-size: 15px;'>Si vous pensez qu'il s'agit d'une erreur ou souhaitez réactiver votre compte, veuillez contacter notre équipe de support.</p>" +
                "                <div style='text-align: center; margin: 30px 0;'>" +
                "                    <a href='mailto:support@smartagriculture.com' style='display: inline-block; padding: 15px 40px; background: #e74c3c; color: white; text-decoration: none; border-radius: 5px; font-weight: bold;'>Contact Support | Contacter le Support</a>" +
                "                </div>" +
                "            </td></tr>" +
                "            <tr><td style='padding: 30px 40px; text-align: center; background: #f9f9f9; border-top: 1px solid #e0e0e0;'>" +
                "                <p style='margin: 0; color: #333; font-weight: bold;'>Smart Agriculture Team</p>" +
                "                <p style='margin: 5px 0 0 0; color: #999; font-size: 12px;'>© 2024 Smart Agriculture. All rights reserved.</p>" +
                "            </td></tr>" +
                "        </table>" +
                "    </td></tr></table>" +
                "</body></html>";
    }

    // ==================== ROLE CHANGE NOTIFICATION EMAIL ====================

    /**
     * Sends role change notification
     */
    public void sendRoleChangeNotification(UserDTO user, String previousRole, String newRole) {
        String subject = "🔄 Role Update | Mise à Jour du Rôle";
        String htmlBody = createRoleChangeEmailTemplate(user, previousRole, newRole);
        sendHtmlEmail(user.getEmail(), subject, htmlBody);
        logger.info("Role change notification sent to: {}", user.getEmail());
    }

    private String createRoleChangeEmailTemplate(UserDTO user, String previousRole, String newRole) {
        return "<!DOCTYPE html>" +
                "<html><body style='margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;'>" +
                "    <table role='presentation' style='width: 100%; border-collapse: collapse;'><tr><td style='padding: 40px 0;'>" +
                "        <table role='presentation' style='width: 600px; margin: 0 auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1);'>" +
                "            <tr><td style='background: linear-gradient(135deg, #9b59b6 0%, #8e44ad 100%); padding: 40px; text-align: center;'>" +
                "                <h1 style='color: white; margin: 0; font-size: 28px;'>🔄 Role Updated</h1>" +
                "                <p style='color: #f4ecf7; margin: 10px 0 0 0;'>Rôle Mis à Jour</p>" +
                "            </td></tr>" +
                "            <tr><td style='padding: 40px;'>" +
                "                <h2 style='color: #8e44ad; margin: 0 0 20px 0;'>Your Role Has Been Updated</h2>" +
                "                <p style='color: #333; font-size: 16px;'>Dear <strong>" + user.getFullName() + "</strong>,</p>" +
                "                <p style='color: #555; font-size: 15px;'>Your role in the Smart Agriculture Management System has been updated.</p>" +
                "                <div style='background: #f4ecf7; border-left: 4px solid #9b59b6; padding: 20px; margin: 20px 0; border-radius: 5px;'>" +
                "                    <p style='margin: 5px 0; color: #333;'><strong>Previous Role:</strong> <span style='color: #e74c3c;'>" + previousRole + "</span></p>" +
                "                    <p style='margin: 5px 0; color: #333;'><strong>New Role:</strong> <span style='color: #27ae60; font-size: 18px;'>" + newRole + "</span></p>" +
                "                </div>" +
                "                <p style='color: #555; font-size: 15px;'>The change will take effect the next time you log in. You may need to log out and log back in to see the changes.</p>" +
                "                <div style='height: 2px; background: linear-gradient(to right, transparent, #9b59b6, transparent); margin: 30px 0;'></div>" +
                "                <h2 style='color: #8e44ad; margin: 0 0 20px 0;'>Votre Rôle a Été Mis à Jour</h2>" +
                "                <p style='color: #333; font-size: 16px;'>Cher(ère) <strong>" + user.getFullName() + "</strong>,</p>" +
                "                <p style='color: #555; font-size: 15px;'>Votre rôle dans le Système de Gestion Agricole Intelligente a été mis à jour.</p>" +
                "                <p style='color: #555; font-size: 15px;'>Le changement prendra effet lors de votre prochaine connexion. Vous devrez peut-être vous déconnecter et vous reconnecter.</p>" +
                "                <div style='text-align: center; margin: 30px 0;'>" +
                "                    <a href='http://localhost:1010/login' style='display: inline-block; padding: 15px 40px; background: #9b59b6; color: white; text-decoration: none; border-radius: 5px; font-weight: bold;'>Login Again | Se Reconnecter</a>" +
                "                </div>" +
                "            </td></tr>" +
                "            <tr><td style='padding: 30px 40px; text-align: center; background: #f9f9f9; border-top: 1px solid #e0e0e0;'>" +
                "                <p style='margin: 0; color: #333; font-weight: bold;'>Smart Agriculture Team</p>" +
                "                <p style='margin: 5px 0 0 0; color: #999; font-size: 12px;'>© 2024 Smart Agriculture. All rights reserved.</p>" +
                "            </td></tr>" +
                "        </table>" +
                "    </td></tr></table>" +
                "</body></html>";
    }

    // ==================== ROLE-SPECIFIC NOTIFICATIONS ====================

    /**
     * Sends farming alert notification - FOR FARMERS
     */
    public void sendFarmingAlert(UserDTO farmer, String alertTitle, String alertMessage) {
        if (!farmer.isFarmer()) {
            logger.warn("Attempted to send farming alert to non-farmer user: {}", farmer.getEmail());
            return;
        }
        String subject = "🌾 Farming Alert: " + alertTitle;
        String htmlBody = createFarmingAlertEmailTemplate(farmer, alertTitle, alertMessage);
        sendHtmlEmail(farmer.getEmail(), subject, htmlBody);
        logger.info("Farming alert sent to: {}", farmer.getEmail());
    }

    private String createFarmingAlertEmailTemplate(UserDTO farmer, String alertTitle, String alertMessage) {
        return "<!DOCTYPE html>" +
                "<html><body style='margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;'>" +
                "    <table role='presentation' style='width: 100%; border-collapse: collapse;'><tr><td style='padding: 40px 0;'>" +
                "        <table role='presentation' style='width: 600px; margin: 0 auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1);'>" +
                "            <tr><td style='background: linear-gradient(135deg, #f39c12 0%, #e67e22 100%); padding: 40px; text-align: center;'>" +
                "                <h1 style='color: white; margin: 0; font-size: 28px;'>🌾 Farming Alert</h1>" +
                "                <p style='color: #fef5e7; margin: 10px 0 0 0;'>Alerte Agricole</p>" +
                "            </td></tr>" +
                "            <tr><td style='padding: 40px;'>" +
                "                <h2 style='color: #e67e22; margin: 0 0 20px 0;'>" + alertTitle + "</h2>" +
                "                <p style='color: #333; font-size: 16px;'>Dear Farmer <strong>" + farmer.getFullName() + "</strong>,</p>" +
                "                <div style='background: #fef5e7; border-left: 4px solid #f39c12; padding: 20px; margin: 20px 0; border-radius: 5px;'>" +
                "                    <p style='margin: 0; color: #555; font-size: 15px; line-height: 1.6;'>" + alertMessage + "</p>" +
                "                </div>" +
                "                <p style='color: #555; font-size: 15px;'>Please take appropriate action as needed.</p>" +
                "            </td></tr>" +
                "            <tr><td style='padding: 30px 40px; text-align: center; background: #f9f9f9; border-top: 1px solid #e0e0e0;'>" +
                "                <p style='margin: 0; color: #333; font-weight: bold;'>Smart Agriculture Team</p>" +
                "                <p style='margin: 5px 0 0 0; color: #999; font-size: 12px;'>© 2024 Smart Agriculture. All rights reserved.</p>" +
                "            </td></tr>" +
                "        </table>" +
                "    </td></tr></table>" +
                "</body></html>";
    }

    /**
     * Sends market update - FOR BUYERS
     */
    public void sendMarketUpdate(UserDTO buyer, String updateTitle, String updateContent) {
        if (!buyer.isBuyer()) {
            logger.warn("Attempted to send market update to non-buyer user: {}", buyer.getEmail());
            return;
        }
        String subject = "💰 Market Update: " + updateTitle;
        String htmlBody = createMarketUpdateEmailTemplate(buyer, updateTitle, updateContent);
        sendHtmlEmail(buyer.getEmail(), subject, htmlBody);
        logger.info("Market update sent to: {}", buyer.getEmail());
    }

    private String createMarketUpdateEmailTemplate(UserDTO buyer, String updateTitle, String updateContent) {
        return "<!DOCTYPE html>" +
                "<html><body style='margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;'>" +
                "    <table role='presentation' style='width: 100%; border-collapse: collapse;'><tr><td style='padding: 40px 0;'>" +
                "        <table role='presentation' style='width: 600px; margin: 0 auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1);'>" +
                "            <tr><td style='background: linear-gradient(135deg, #16a085 0%, #138d75 100%); padding: 40px; text-align: center;'>" +
                "                <h1 style='color: white; margin: 0; font-size: 28px;'>💰 Market Update</h1>" +
                "                <p style='color: #d5f4e6; margin: 10px 0 0 0;'>Mise à Jour du Marché</p>" +
                "            </td></tr>" +
                "            <tr><td style='padding: 40px;'>" +
                "                <h2 style='color: #138d75; margin: 0 0 20px 0;'>" + updateTitle + "</h2>" +
                "                <p style='color: #333; font-size: 16px;'>Dear Buyer <strong>" + buyer.getFullName() + "</strong>,</p>" +
                "                <div style='background: #d5f4e6; border-left: 4px solid #16a085; padding: 20px; margin: 20px 0; border-radius: 5px;'>" +
                "                    <p style='margin: 0; color: #555; font-size: 15px; line-height: 1.6;'>" + updateContent + "</p>" +
                "                </div>" +
                "                <p style='color: #555; font-size: 15px;'>Stay informed about market trends and opportunities.</p>" +
                "            </td></tr>" +
                "            <tr><td style='padding: 30px 40px; text-align: center; background: #f9f9f9; border-top: 1px solid #e0e0e0;'>" +
                "                <p style='margin: 0; color: #333; font-weight: bold;'>Smart Agriculture Team</p>" +
                "                <p style='margin: 5px 0 0 0; color: #999; font-size: 12px;'>© 2024 Smart Agriculture. All rights reserved.</p>" +
                "            </td></tr>" +
                "        </table>" +
                "    </td></tr></table>" +
                "</body></html>";
    }

    /**
     * Sends analytical report - FOR ANALYSTS
     */
    public void sendAnalyticalReport(UserDTO analyst, String reportTitle, String reportSummary) {
        if (!analyst.isAnalyst()) {
            logger.warn("Attempted to send analytical report to non-analyst user: {}", analyst.getEmail());
            return;
        }
        String subject = "📊 Analytical Report: " + reportTitle;
        String htmlBody = createAnalyticalReportEmailTemplate(analyst, reportTitle, reportSummary);
        sendHtmlEmail(analyst.getEmail(), subject, htmlBody);
        logger.info("Analytical report sent to: {}", analyst.getEmail());
    }

    private String createAnalyticalReportEmailTemplate(UserDTO analyst, String reportTitle, String reportSummary) {
        return "<!DOCTYPE html>" +
                "<html><body style='margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;'>" +
                "    <table role='presentation' style='width: 100%; border-collapse: collapse;'><tr><td style='padding: 40px 0;'>" +
                "        <table role='presentation' style='width: 600px; margin: 0 auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1);'>" +
                "            <tr><td style='background: linear-gradient(135deg, #3498db 0%, #2980b9 100%); padding: 40px; text-align: center;'>" +
                "                <h1 style='color: white; margin: 0; font-size: 28px;'>📊 Analytical Report</h1>" +
                "                <p style='color: #ebf5fb; margin: 10px 0 0 0;'>Rapport Analytique</p>" +
                "            </td></tr>" +
                "            <tr><td style='padding: 40px;'>" +
                "                <h2 style='color: #2980b9; margin: 0 0 20px 0;'>" + reportTitle + "</h2>" +
                "                <p style='color: #333; font-size: 16px;'>Dear Analyst <strong>" + analyst.getFullName() + "</strong>,</p>" +
                "                <p style='color: #555; font-size: 15px;'>A new analytical report is available for your review.</p>" +
                "                <div style='background: #ebf5fb; border-left: 4px solid #3498db; padding: 20px; margin: 20px 0; border-radius: 5px;'>" +
                "                    <h3 style='color: #2980b9; margin: 0 0 10px 0;'>Summary:</h3>" +
                "                    <p style='margin: 0; color: #555; font-size: 15px; line-height: 1.6;'>" + reportSummary + "</p>" +
                "                </div>" +
                "                <p style='color: #555; font-size: 15px;'>Please review the report and provide your insights as needed.</p>" +
                "            </td></tr>" +
                "            <tr><td style='padding: 30px 40px; text-align: center; background: #f9f9f9; border-top: 1px solid #e0e0e0;'>" +
                "                <p style='margin: 0; color: #333; font-weight: bold;'>Smart Agriculture Team</p>" +
                "                <p style='margin: 5px 0 0 0; color: #999; font-size: 12px;'>© 2024 Smart Agriculture. All rights reserved.</p>" +
                "            </td></tr>" +
                "        </table>" +
                "    </td></tr></table>" +
                "</body></html>";
    }

    /**
     * Sends government notification - FOR GOVERNMENT OFFICIALS
     */
    public void sendGovernmentNotification(UserDTO govUser, String notificationTitle, String notificationContent) {
        if (!govUser.isGovernment()) {
            logger.warn("Attempted to send government notification to non-government user: {}", govUser.getEmail());
            return;
        }
        String subject = "🏛️ Government Notification: " + notificationTitle;
        String htmlBody = createGovernmentNotificationEmailTemplate(govUser, notificationTitle, notificationContent);
        sendHtmlEmail(govUser.getEmail(), subject, htmlBody);
        logger.info("Government notification sent to: {}", govUser.getEmail());
    }

    private String createGovernmentNotificationEmailTemplate(UserDTO govUser, String notificationTitle, String notificationContent) {
        return "<!DOCTYPE html>" +
                "<html><body style='margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;'>" +
                "    <table role='presentation' style='width: 100%; border-collapse: collapse;'><tr><td style='padding: 40px 0;'>" +
                "        <table role='presentation' style='width: 600px; margin: 0 auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1);'>" +
                "            <tr><td style='background: linear-gradient(135deg, #34495e 0%, #2c3e50 100%); padding: 40px; text-align: center;'>" +
                "                <h1 style='color: white; margin: 0; font-size: 28px;'>🏛️ Official Notification</h1>" +
                "                <p style='color: #ecf0f1; margin: 10px 0 0 0;'>Notification Officielle</p>" +
                "            </td></tr>" +
                "            <tr><td style='padding: 40px;'>" +
                "                <h2 style='color: #2c3e50; margin: 0 0 20px 0;'>" + notificationTitle + "</h2>" +
                "                <p style='color: #333; font-size: 16px;'>Dear <strong>" + govUser.getFullName() + "</strong>,</p>" +
                "                <div style='background: #ecf0f1; border-left: 4px solid #34495e; padding: 20px; margin: 20px 0; border-radius: 5px;'>" +
                "                    <p style='margin: 0; color: #555; font-size: 15px; line-height: 1.6;'>" + notificationContent + "</p>" +
                "                </div>" +
                "                <div style='background: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0; border-radius: 5px;'>" +
                "                    <p style='margin: 0; color: #856404; font-size: 14px;'><strong>⚠️ Important:</strong> This notification may require official action or review.</p>" +
                "                </div>" +
                "            </td></tr>" +
                "            <tr><td style='padding: 30px 40px; text-align: center; background: #f9f9f9; border-top: 1px solid #e0e0e0;'>" +
                "                <p style='margin: 0; color: #333; font-weight: bold;'>Smart Agriculture Government Relations</p>" +
                "                <p style='margin: 5px 0 0 0; color: #999; font-size: 12px;'>© 2024 Smart Agriculture. All rights reserved.</p>" +
                "            </td></tr>" +
                "        </table>" +
                "    </td></tr></table>" +
                "</body></html>";
    }

    // ==================== SYSTEM MAINTENANCE EMAIL ====================

    /**
     * Sends system maintenance notification
     */
    public void sendSystemMaintenanceNotification(UserDTO user, String maintenanceDate, String duration) {
        String subject = "🔧 Scheduled System Maintenance | Maintenance Programmée";
        String htmlBody = createMaintenanceEmailTemplate(user, maintenanceDate, duration);
        sendHtmlEmail(user.getEmail(), subject, htmlBody);
        logger.info("System maintenance notification sent to: {}", user.getEmail());
    }

    private String createMaintenanceEmailTemplate(UserDTO user, String maintenanceDate, String duration) {
        return "<!DOCTYPE html>" +
                "<html><body style='margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;'>" +
                "    <table role='presentation' style='width: 100%; border-collapse: collapse;'><tr><td style='padding: 40px 0;'>" +
                "        <table role='presentation' style='width: 600px; margin: 0 auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1);'>" +
                "            <tr><td style='background: linear-gradient(135deg, #95a5a6 0%, #7f8c8d 100%); padding: 40px; text-align: center;'>" +
                "                <h1 style='color: white; margin: 0; font-size: 28px;'>🔧 System Maintenance</h1>" +
                "                <p style='color: #ecf0f1; margin: 10px 0 0 0;'>Maintenance Système</p>" +
                "            </td></tr>" +
                "            <tr><td style='padding: 40px;'>" +
                "                <h2 style='color: #7f8c8d; margin: 0 0 20px 0;'>Scheduled Maintenance Notice</h2>" +
                "                <p style='color: #333; font-size: 16px;'>Dear <strong>" + user.getFullName() + "</strong>,</p>" +
                "                <p style='color: #555; font-size: 15px;'>We would like to inform you about scheduled system maintenance.</p>" +
                "                <div style='background: #ecf0f1; border-left: 4px solid #95a5a6; padding: 20px; margin: 20px 0; border-radius: 5px;'>" +
                "                    <p style='margin: 5px 0; color: #333;'><strong>📅 Maintenance Date:</strong> " + maintenanceDate + "</p>" +
                "                    <p style='margin: 5px 0; color: #333;'><strong>⏱️ Expected Duration:</strong> " + duration + "</p>" +
                "                </div>" +
                "                <div style='background: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0; border-radius: 5px;'>" +
                "                    <p style='margin: 0; color: #856404; font-size: 14px;'><strong>⚠️ Note:</strong> The system may be temporarily unavailable during this time.</p>" +
                "                </div>" +
                "                <div style='height: 2px; background: linear-gradient(to right, transparent, #95a5a6, transparent); margin: 30px 0;'></div>" +
                "                <h2 style='color: #7f8c8d; margin: 0 0 20px 0;'>Avis de Maintenance Programmée</h2>" +
                "                <p style='color: #555; font-size: 15px;'>Le système peut être temporairement indisponible pendant cette période. Nous nous excusons pour la gêne occasionnée.</p>" +
                "            </td></tr>" +
                "            <tr><td style='padding: 30px 40px; text-align: center; background: #f9f9f9; border-top: 1px solid #e0e0e0;'>" +
                "                <p style='margin: 0; color: #333; font-weight: bold;'>Smart Agriculture IT Team</p>" +
                "                <p style='margin: 5px 0 0 0; color: #999; font-size: 12px;'>© 2024 Smart Agriculture. All rights reserved.</p>" +
                "            </td></tr>" +
                "        </table>" +
                "    </td></tr></table>" +
                "</body></html>";
    }

    // ==================== HELPER METHODS ====================

    /**
     * Get role display name in English
     */
    private String getRoleDisplayName(SmartAgricultural.Management.Model.User.Role role) {
        switch (role) {
            case ADMIN: return "Administrator";
            case FARMER: return "Farmer";
            case BUYER: return "Buyer";
            case ANALYST: return "Data Analyst";
            case GOVERNMENT: return "Government Official";
            default: return role.name();
        }
    }

    /**
     * Get role display name in French
     */
    private String getRoleDisplayNameFrench(SmartAgricultural.Management.Model.User.Role role) {
        switch (role) {
            case ADMIN: return "Administrateur";
            case FARMER: return "Agriculteur";
            case BUYER: return "Acheteur";
            case ANALYST: return "Analyste de Données";
            case GOVERNMENT: return "Responsable Gouvernemental";
            default: return role.name();
        }
    }

    // ==================== ADDITIONAL UTILITY METHODS ====================

    /**
     * Sends an email with BCC recipients
     */
    public void sendEmailWithBCC(String to, String[] bccRecipients, String subject, String body) {
        try {
            logger.info("Preparing to send email to: {} with {} BCC recipients", to, bccRecipients != null ? bccRecipients.length : 0);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false);

            if (bccRecipients != null && bccRecipients.length > 0) {
                String[] validBccRecipients = java.util.Arrays.stream(bccRecipients)
                        .filter(bcc -> bcc != null && !bcc.trim().isEmpty())
                        .toArray(String[]::new);

                if (validBccRecipients.length > 0) {
                    helper.setBcc(validBccRecipients);
                    logger.info("Added {} BCC recipients", validBccRecipients.length);
                }
            }

            mailSender.send(message);
            logger.info("Email sent successfully to: {} with BCC recipients", to);
        } catch (MessagingException e) {
            logger.error("Failed to send email with BCC to: " + to, e);
            throw new RuntimeException("Failed to send email with BCC", e);
        }
    }

    /**
     * Sends an email with both CC and BCC recipients
     */
    public void sendEmailWithCCAndBCC(String to, String[] ccRecipients, String[] bccRecipients, String subject, String body) {
        try {
            logger.info("Preparing to send email to: {} with {} CC and {} BCC recipients",
                    to, ccRecipients != null ? ccRecipients.length : 0, bccRecipients != null ? bccRecipients.length : 0);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false);

            if (ccRecipients != null && ccRecipients.length > 0) {
                String[] validCcRecipients = java.util.Arrays.stream(ccRecipients)
                        .filter(cc -> cc != null && !cc.trim().isEmpty())
                        .toArray(String[]::new);

                if (validCcRecipients.length > 0) {
                    helper.setCc(validCcRecipients);
                    logger.info("Added {} CC recipients", validCcRecipients.length);
                }
            }

            if (bccRecipients != null && bccRecipients.length > 0) {
                String[] validBccRecipients = java.util.Arrays.stream(bccRecipients)
                        .filter(bcc -> bcc != null && !bcc.trim().isEmpty())
                        .toArray(String[]::new);

                if (validBccRecipients.length > 0) {
                    helper.setBcc(validBccRecipients);
                    logger.info("Added {} BCC recipients", validBccRecipients.length);
                }
            }

            mailSender.send(message);
            logger.info("Email sent successfully to: {} with CC and BCC recipients", to);
        } catch (MessagingException e) {
            logger.error("Failed to send email with CC and BCC to: " + to, e);
            throw new RuntimeException("Failed to send email with CC and BCC", e);
        }
    }

    /**
     * Sends an HTML email with CC recipients
     */
    public void sendHtmlEmailWithCC(String to, String[] ccRecipients, String subject, String htmlBody) {
        try {
            logger.info("Preparing to send HTML email to: {} with {} CC recipients", to, ccRecipients != null ? ccRecipients.length : 0);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            if (ccRecipients != null && ccRecipients.length > 0) {
                String[] validCcRecipients = java.util.Arrays.stream(ccRecipients)
                        .filter(cc -> cc != null && !cc.trim().isEmpty())
                        .toArray(String[]::new);

                if (validCcRecipients.length > 0) {
                    helper.setCc(validCcRecipients);
                    logger.info("Added {} CC recipients", validCcRecipients.length);
                }
            }

            mailSender.send(message);
            logger.info("HTML email sent successfully to: {} with CC recipients", to);
        } catch (MessagingException e) {
            logger.error("Failed to send HTML email with CC to: " + to, e);
            throw new RuntimeException("Failed to send HTML email with CC", e);
        }
    }

    /**
     * Sends a notification email to multiple recipients
     */
    public void sendNotificationEmail(String[] recipients, String subject, String body) {
        if (recipients == null || recipients.length == 0) {
            logger.warn("No recipients provided for notification email");
            return;
        }

        String[] validRecipients = java.util.Arrays.stream(recipients)
                .filter(recipient -> recipient != null && !recipient.trim().isEmpty())
                .toArray(String[]::new);

        if (validRecipients.length == 0) {
            logger.warn("No valid recipients found for notification email");
            return;
        }

        if (validRecipients.length == 1) {
            sendEmail(validRecipients[0], subject, body);
        } else {
            String primaryRecipient = validRecipients[0];
            String[] ccRecipients = java.util.Arrays.copyOfRange(validRecipients, 1, validRecipients.length);
            sendEmailWithCC(primaryRecipient, ccRecipients, subject, body);
        }
    }





    // ==================== TRANSACTION NOTIFICATION EMAILS ====================

    /**
     * Sends transaction creation notification to Farmer and Buyer
     */
    public void sendTransactionCreatedNotification(UserDTO farmer, UserDTO buyer, Transaction transaction) {
        try {
            // Email to Farmer
            String farmerSubject = "🌾 New Transaction Created | Nouvelle Transaction Créée";
            String farmerBody = createTransactionCreatedEmailForFarmer(farmer, buyer, transaction);
            sendHtmlEmail(farmer.getEmail(), farmerSubject, farmerBody);
            logger.info("Transaction creation email sent to farmer: {}", farmer.getEmail());

            // Email to Buyer
            String buyerSubject = "💰 New Transaction Created | Nouvelle Transaction Créée";
            String buyerBody = createTransactionCreatedEmailForBuyer(buyer, farmer, transaction);
            sendHtmlEmail(buyer.getEmail(), buyerSubject, buyerBody);
            logger.info("Transaction creation email sent to buyer: {}", buyer.getEmail());

        } catch (Exception e) {
            logger.error("Failed to send transaction creation emails", e);
        }
    }

    /**
     * Sends transaction confirmation notification
     */
    public void sendTransactionConfirmedNotification(UserDTO farmer, UserDTO buyer, Transaction transaction) {
        try {
            String subject = "✅ Transaction Confirmed | Transaction Confirmée - " + transaction.getTransactionCode();

            // Email to Farmer
            String farmerBody = createTransactionConfirmedEmailForFarmer(farmer, buyer, transaction);
            sendHtmlEmail(farmer.getEmail(), subject, farmerBody);

            // Email to Buyer
            String buyerBody = createTransactionConfirmedEmailForBuyer(buyer, farmer, transaction);
            sendHtmlEmail(buyer.getEmail(), subject, buyerBody);

            logger.info("Transaction confirmed emails sent for transaction: {}", transaction.getTransactionCode());
        } catch (Exception e) {
            logger.error("Failed to send transaction confirmed emails", e);
        }
    }

    /**
     * Sends transaction delivered notification
     */
    public void sendTransactionDeliveredNotification(UserDTO farmer, UserDTO buyer, Transaction transaction) {
        try {
            String subject = "🚚 Transaction Delivered | Transaction Livrée - " + transaction.getTransactionCode();

            // Email to Farmer
            String farmerBody = createTransactionDeliveredEmailForFarmer(farmer, buyer, transaction);
            sendHtmlEmail(farmer.getEmail(), subject, farmerBody);

            // Email to Buyer
            String buyerBody = createTransactionDeliveredEmailForBuyer(buyer, farmer, transaction);
            sendHtmlEmail(buyer.getEmail(), subject, buyerBody);

            logger.info("Transaction delivered emails sent for transaction: {}", transaction.getTransactionCode());
        } catch (Exception e) {
            logger.error("Failed to send transaction delivered emails", e);
        }
    }

    /**
     * Sends transaction paid notification
     */
    public void sendTransactionPaidNotification(UserDTO farmer, UserDTO buyer, Transaction transaction) {
        try {
            String subject = "💵 Payment Confirmed | Paiement Confirmé - " + transaction.getTransactionCode();

            // Email to Farmer
            String farmerBody = createTransactionPaidEmailForFarmer(farmer, buyer, transaction);
            sendHtmlEmail(farmer.getEmail(), subject, farmerBody);

            // Email to Buyer
            String buyerBody = createTransactionPaidEmailForBuyer(buyer, farmer, transaction);
            sendHtmlEmail(buyer.getEmail(), subject, buyerBody);

            logger.info("Transaction paid emails sent for transaction: {}", transaction.getTransactionCode());
        } catch (Exception e) {
            logger.error("Failed to send transaction paid emails", e);
        }
    }

    /**
     * Sends transaction cancelled notification
     */
    public void sendTransactionCancelledNotification(UserDTO farmer, UserDTO buyer, Transaction transaction, String reason) {
        try {
            String subject = "❌ Transaction Cancelled | Transaction Annulée - " + transaction.getTransactionCode();

            // Email to Farmer
            String farmerBody = createTransactionCancelledEmailForFarmer(farmer, buyer, transaction, reason);
            sendHtmlEmail(farmer.getEmail(), subject, farmerBody);

            // Email to Buyer
            String buyerBody = createTransactionCancelledEmailForBuyer(buyer, farmer, transaction, reason);
            sendHtmlEmail(buyer.getEmail(), subject, buyerBody);

            logger.info("Transaction cancelled emails sent for transaction: {}", transaction.getTransactionCode());
        } catch (Exception e) {
            logger.error("Failed to send transaction cancelled emails", e);
        }
    }

    /**
     * Sends transaction disputed notification
     */
    public void sendTransactionDisputedNotification(UserDTO farmer, UserDTO buyer, Transaction transaction, String disputeReason) {
        try {
            String subject = "⚠️ Transaction Disputed | Transaction Contestée - " + transaction.getTransactionCode();

            // Email to Farmer
            String farmerBody = createTransactionDisputedEmailForFarmer(farmer, buyer, transaction, disputeReason);
            sendHtmlEmail(farmer.getEmail(), subject, farmerBody);

            // Email to Buyer
            String buyerBody = createTransactionDisputedEmailForBuyer(buyer, farmer, transaction, disputeReason);
            sendHtmlEmail(buyer.getEmail(), subject, buyerBody);

            logger.info("Transaction disputed emails sent for transaction: {}", transaction.getTransactionCode());
        } catch (Exception e) {
            logger.error("Failed to send transaction disputed emails", e);
        }
    }

// ==================== EMAIL TEMPLATES FOR TRANSACTIONS ====================

    private String createTransactionCreatedEmailForFarmer(UserDTO farmer, UserDTO buyer, Transaction transaction) {
        return "<!DOCTYPE html>" +
                "<html><body style='margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;'>" +
                "    <table role='presentation' style='width: 100%; border-collapse: collapse;'><tr><td style='padding: 40px 0;'>" +
                "        <table role='presentation' style='width: 600px; margin: 0 auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1);'>" +
                "            <tr><td style='background: linear-gradient(135deg, #2ecc71 0%, #27ae60 100%); padding: 40px; text-align: center;'>" +
                "                <h1 style='color: white; margin: 0; font-size: 28px;'>🌾 New Transaction Created</h1>" +
                "                <p style='color: #e8f5e9; margin: 10px 0 0 0;'>Nouvelle Transaction Créée</p>" +
                "            </td></tr>" +
                "            <tr><td style='padding: 40px;'>" +
                "                <h2 style='color: #27ae60; margin: 0 0 20px 0;'>Transaction Details | Détails de la Transaction</h2>" +
                "                <p style='color: #333; font-size: 16px;'>Dear Farmer <strong>" + farmer.getFullName() + "</strong>,</p>" +
                "                <p style='color: #555; font-size: 15px;'>A new transaction has been created with buyer <strong>" + buyer.getFullName() + "</strong>.</p>" +
                "                <div style='background: #e8f5e9; border-left: 4px solid #2ecc71; padding: 20px; margin: 20px 0; border-radius: 5px;'>" +
                "                    <p style='margin: 5px 0; color: #333;'><strong>Transaction Code:</strong> " + transaction.getTransactionCode() + "</p>" +
                "                    <p style='margin: 5px 0; color: #333;'><strong>Crop ID:</strong> " + transaction.getCropId() + "</p>" +
                "                    <p style='margin: 5px 0; color: #333;'><strong>Quantity:</strong> " + transaction.getQuantity() + " kg</p>" +
                "                    <p style='margin: 5px 0; color: #333;'><strong>Price per Unit:</strong> " + transaction.getPricePerUnit() + " RWF</p>" +
                "                    <p style='margin: 5px 0; color: #333;'><strong>Total Amount:</strong> " + transaction.getTotalAmount() + " RWF</p>" +
                "                    <p style='margin: 5px 0; color: #333;'><strong>Payment Method:</strong> " + transaction.getPaymentMethod().getDisplayName() + "</p>" +
                "                    <p style='margin: 5px 0; color: #333;'><strong>Status:</strong> " + transaction.getStatus().getDisplayName() + "</p>" +
                "                    <p style='margin: 5px 0; color: #333;'><strong>Delivery Date:</strong> " + (transaction.getDeliveryDate() != null ? transaction.getDeliveryDate().toString() : "To be determined") + "</p>" +
                "                </div>" +
                "                <p style='color: #555; font-size: 15px;'>Buyer Contact: " + buyer.getPhoneNumber() + " | " + buyer.getEmail() + "</p>" +
                "                <div style='height: 2px; background: linear-gradient(to right, transparent, #2ecc71, transparent); margin: 30px 0;'></div>" +
                "                <h2 style='color: #27ae60; margin: 0 0 20px 0;'>Pour l'Agriculteur</h2>" +
                "                <p style='color: #333; font-size: 16px;'>Cher Agriculteur <strong>" + farmer.getFullName() + "</strong>,</p>" +
                "                <p style='color: #555; font-size: 15px;'>Une nouvelle transaction a été créée avec l'acheteur <strong>" + buyer.getFullName() + "</strong>.</p>" +
                "                <p style='color: #555; font-size: 15px;'>Veuillez consulter les détails ci-dessus et préparer votre livraison.</p>" +
                "            </td></tr>" +
                "            <tr><td style='padding: 30px 40px; text-align: center; background: #f9f9f9; border-top: 1px solid #e0e0e0;'>" +
                "                <p style='margin: 0; color: #333; font-weight: bold;'>Smart Agriculture Team</p>" +
                "                <p style='margin: 5px 0 0 0; color: #999; font-size: 12px;'>© 2024 Smart Agriculture. All rights reserved.</p>" +
                "            </td></tr>" +
                "        </table>" +
                "    </td></tr></table>" +
                "</body></html>";
    }

    private String createTransactionCreatedEmailForBuyer(UserDTO buyer, UserDTO farmer, Transaction transaction) {
        return "<!DOCTYPE html>" +
                "<html><body style='margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;'>" +
                "    <table role='presentation' style='width: 100%; border-collapse: collapse;'><tr><td style='padding: 40px 0;'>" +
                "        <table role='presentation' style='width: 600px; margin: 0 auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1);'>" +
                "            <tr><td style='background: linear-gradient(135deg, #16a085 0%, #138d75 100%); padding: 40px; text-align: center;'>" +
                "                <h1 style='color: white; margin: 0; font-size: 28px;'>💰 New Transaction Created</h1>" +
                "                <p style='color: #d5f4e6; margin: 10px 0 0 0;'>Nouvelle Transaction Créée</p>" +
                "            </td></tr>" +
                "            <tr><td style='padding: 40px;'>" +
                "                <h2 style='color: #138d75; margin: 0 0 20px 0;'>Transaction Details | Détails de la Transaction</h2>" +
                "                <p style='color: #333; font-size: 16px;'>Dear Buyer <strong>" + buyer.getFullName() + "</strong>,</p>" +
                "                <p style='color: #555; font-size: 15px;'>Your transaction with farmer <strong>" + farmer.getFullName() + "</strong> has been created successfully.</p>" +
                "                <div style='background: #d5f4e6; border-left: 4px solid #16a085; padding: 20px; margin: 20px 0; border-radius: 5px;'>" +
                "                    <p style='margin: 5px 0; color: #333;'><strong>Transaction Code:</strong> " + transaction.getTransactionCode() + "</p>" +
                "                    <p style='margin: 5px 0; color: #333;'><strong>Crop ID:</strong> " + transaction.getCropId() + "</p>" +
                "                    <p style='margin: 5px 0; color: #333;'><strong>Quantity:</strong> " + transaction.getQuantity() + " kg</p>" +
                "                    <p style='margin: 5px 0; color: #333;'><strong>Price per Unit:</strong> " + transaction.getPricePerUnit() + " RWF</p>" +
                "                    <p style='margin: 5px 0; color: #333;'><strong>Total Amount:</strong> " + transaction.getTotalAmount() + " RWF</p>" +
                "                    <p style='margin: 5px 0; color: #333;'><strong>Payment Method:</strong> " + transaction.getPaymentMethod().getDisplayName() + "</p>" +
                "                    <p style='margin: 5px 0; color: #333;'><strong>Status:</strong> " + transaction.getStatus().getDisplayName() + "</p>" +
                "                    <p style='margin: 5px 0; color: #333;'><strong>Delivery Date:</strong> " + (transaction.getDeliveryDate() != null ? transaction.getDeliveryDate().toString() : "To be determined") + "</p>" +
                "                </div>" +
                "                <p style='color: #555; font-size: 15px;'>Farmer Contact: " + farmer.getPhoneNumber() + " | " + farmer.getEmail() + "</p>" +
                "                <div style='height: 2px; background: linear-gradient(to right, transparent, #16a085, transparent); margin: 30px 0;'></div>" +
                "                <h2 style='color: #138d75; margin: 0 0 20px 0;'>Pour l'Acheteur</h2>" +
                "                <p style='color: #333; font-size: 16px;'>Cher Acheteur <strong>" + buyer.getFullName() + "</strong>,</p>" +
                "                <p style='color: #555; font-size: 15px;'>Votre transaction avec l'agriculteur <strong>" + farmer.getFullName() + "</strong> a été créée avec succès.</p>" +
                "            </td></tr>" +
                "            <tr><td style='padding: 30px 40px; text-align: center; background: #f9f9f9; border-top: 1px solid #e0e0e0;'>" +
                "                <p style='margin: 0; color: #333; font-weight: bold;'>Smart Agriculture Team</p>" +
                "                <p style='margin: 5px 0 0 0; color: #999; font-size: 12px;'>© 2024 Smart Agriculture. All rights reserved.</p>" +
                "            </td></tr>" +
                "        </table>" +
                "    </td></tr></table>" +
                "</body></html>";
    }

    private String createTransactionConfirmedEmailForFarmer(UserDTO farmer, UserDTO buyer, Transaction transaction) {
        return createStatusChangeEmail(farmer, buyer, transaction, "✅ Transaction Confirmed", "#2ecc71",
                "Your transaction has been confirmed! Please prepare for delivery.",
                "Votre transaction a été confirmée ! Veuillez préparer la livraison.");
    }

    private String createTransactionConfirmedEmailForBuyer(UserDTO buyer, UserDTO farmer, Transaction transaction) {
        return createStatusChangeEmail(buyer, farmer, transaction, "✅ Transaction Confirmed", "#16a085",
                "The transaction has been confirmed. The farmer will prepare your order.",
                "La transaction a été confirmée. L'agriculteur préparera votre commande.");
    }

    private String createTransactionDeliveredEmailForFarmer(UserDTO farmer, UserDTO buyer, Transaction transaction) {
        return createStatusChangeEmail(farmer, buyer, transaction, "🚚 Transaction Delivered", "#3498db",
                "The transaction has been marked as delivered. Awaiting payment confirmation.",
                "La transaction a été marquée comme livrée. En attente de confirmation de paiement.");
    }

    private String createTransactionDeliveredEmailForBuyer(UserDTO buyer, UserDTO farmer, Transaction transaction) {
        return createStatusChangeEmail(buyer, farmer, transaction, "🚚 Transaction Delivered", "#3498db",
                "Your order has been delivered. Please confirm receipt and proceed with payment.",
                "Votre commande a été livrée. Veuillez confirmer la réception et procéder au paiement.");
    }

    private String createTransactionPaidEmailForFarmer(UserDTO farmer, UserDTO buyer, Transaction transaction) {
        return createStatusChangeEmail(farmer, buyer, transaction, "💵 Payment Confirmed", "#27ae60",
                "Payment has been confirmed for this transaction. Thank you for your business!",
                "Le paiement a été confirmé pour cette transaction. Merci pour votre business !");
    }

    private String createTransactionPaidEmailForBuyer(UserDTO buyer, UserDTO farmer, Transaction transaction) {
        return createStatusChangeEmail(buyer, farmer, transaction, "💵 Payment Confirmed", "#27ae60",
                "Your payment has been confirmed. Transaction completed successfully!",
                "Votre paiement a été confirmé. Transaction terminée avec succès !");
    }

    private String createTransactionCancelledEmailForFarmer(UserDTO farmer, UserDTO buyer, Transaction transaction, String reason) {
        String reasonText = reason != null && !reason.trim().isEmpty() ?
                "<p style='margin: 5px 0; color: #c0392b;'><strong>Cancellation Reason:</strong> " + reason + "</p>" : "";

        return createStatusChangeEmail(farmer, buyer, transaction, "❌ Transaction Cancelled", "#e74c3c",
                "This transaction has been cancelled. " + reasonText,
                "Cette transaction a été annulée. " + reasonText);
    }

    private String createTransactionCancelledEmailForBuyer(UserDTO buyer, UserDTO farmer, Transaction transaction, String reason) {
        String reasonText = reason != null && !reason.trim().isEmpty() ?
                "<p style='margin: 5px 0; color: #c0392b;'><strong>Reason:</strong> " + reason + "</p>" : "";

        return createStatusChangeEmail(buyer, farmer, transaction, "❌ Transaction Cancelled", "#e74c3c",
                "This transaction has been cancelled. " + reasonText,
                "Cette transaction a été annulée. " + reasonText);
    }

    private String createTransactionDisputedEmailForFarmer(UserDTO farmer, UserDTO buyer, Transaction transaction, String disputeReason) {
        String disputeText = disputeReason != null && !disputeReason.trim().isEmpty() ?
                "<p style='margin: 5px 0; color: #d35400;'><strong>Dispute Reason:</strong> " + disputeReason + "</p>" : "";

        return createStatusChangeEmail(farmer, buyer, transaction, "⚠️ Transaction Disputed", "#e67e22",
                "This transaction has been disputed. Please contact support for resolution. " + disputeText,
                "Cette transaction a été contestée. Veuillez contacter le support pour résolution. " + disputeText);
    }

    private String createTransactionDisputedEmailForBuyer(UserDTO buyer, UserDTO farmer, Transaction transaction, String disputeReason) {
        String disputeText = disputeReason != null && !disputeReason.trim().isEmpty() ?
                "<p style='margin: 5px 0; color: #d35400;'><strong>Dispute Reason:</strong> " + disputeReason + "</p>" : "";

        return createStatusChangeEmail(buyer, farmer, transaction, "⚠️ Transaction Disputed", "#e67e22",
                "This transaction has been disputed. Our team will contact you shortly. " + disputeText,
                "Cette transaction a été contestée. Notre équipe vous contactera bientôt. " + disputeText);
    }

    private String createStatusChangeEmail(UserDTO recipient, UserDTO otherParty, Transaction transaction,
                                           String title, String color, String message, String messageFr) {
        return "<!DOCTYPE html>" +
                "<html><body style='margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;'>" +
                "    <table role='presentation' style='width: 100%; border-collapse: collapse;'><tr><td style='padding: 40px 0;'>" +
                "        <table role='presentation' style='width: 600px; margin: 0 auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1);'>" +
                "            <tr><td style='background: " + color + "; padding: 40px; text-align: center;'>" +
                "                <h1 style='color: white; margin: 0; font-size: 28px;'>" + title + "</h1>" +
                "            </td></tr>" +
                "            <tr><td style='padding: 40px;'>" +
                "                <p style='color: #333; font-size: 16px;'>Dear <strong>" + recipient.getFullName() + "</strong>,</p>" +
                "                <p style='color: #555; font-size: 15px;'>" + message + "</p>" +
                "                <div style='background: #f8f9fa; border-left: 4px solid " + color + "; padding: 20px; margin: 20px 0; border-radius: 5px;'>" +
                "                    <p style='margin: 5px 0; color: #333;'><strong>Transaction Code:</strong> " + transaction.getTransactionCode() + "</p>" +
                "                    <p style='margin: 5px 0; color: #333;'><strong>Status:</strong> " + transaction.getStatus().getDisplayName() + "</p>" +
                "                    <p style='margin: 5px 0; color: #333;'><strong>Total Amount:</strong> " + transaction.getTotalAmount() + " RWF</p>" +
                "                </div>" +
                "                <div style='height: 2px; background: linear-gradient(to right, transparent, " + color + ", transparent); margin: 30px 0;'></div>" +
                "                <p style='color: #333; font-size: 16px;'>Cher(ère) <strong>" + recipient.getFullName() + "</strong>,</p>" +
                "                <p style='color: #555; font-size: 15px;'>" + messageFr + "</p>" +
                "            </td></tr>" +
                "            <tr><td style='padding: 30px 40px; text-align: center; background: #f9f9f9; border-top: 1px solid #e0e0e0;'>" +
                "                <p style='margin: 0; color: #333; font-weight: bold;'>Smart Agriculture Team</p>" +
                "                <p style='margin: 5px 0 0 0; color: #999; font-size: 12px;'>© 2024 Smart Agriculture. All rights reserved.</p>" +
                "            </td></tr>" +
                "        </table>" +
                "    </td></tr></table>" +
                "</body></html>";
    }


    /**
     * Sends a bulk email to multiple recipients (each as individual emails)
     */
    public void sendBulkEmail(String[] recipients, String subject, String body) {
        if (recipients == null || recipients.length == 0) {
            logger.warn("No recipients provided for bulk email");
            return;
        }

        int successCount = 0;
        int failureCount = 0;

        for (String recipient : recipients) {
            if (recipient != null && !recipient.trim().isEmpty()) {
                try {
                    sendEmail(recipient, subject, body);
                    successCount++;
                } catch (Exception e) {
                    logger.error("Failed to send bulk email to: " + recipient, e);
                    failureCount++;
                }
            }
        }

        logger.info("Bulk email completed: {} successful, {} failed", successCount, failureCount);
    }

    /**
     * Validates email address format
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        String emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailPattern);
    }

    /**
     * Sends an emergency alert email with high priority
     */
    public void sendEmergencyAlert(String to, String[] ccRecipients, String subject, String body) {
        try {
            logger.info("Preparing to send EMERGENCY email to: {} with {} CC recipients", to, ccRecipients != null ? ccRecipients.length : 0);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("🚨 URGENT: " + subject);
            helper.setText(body, false);

            // Set high priority
            message.setHeader("X-Priority", "1");
            message.setHeader("X-MSMail-Priority", "High");
            message.setHeader("Importance", "High");

            if (ccRecipients != null && ccRecipients.length > 0) {
                String[] validCcRecipients = java.util.Arrays.stream(ccRecipients)
                        .filter(cc -> cc != null && !cc.trim().isEmpty())
                        .toArray(String[]::new);

                if (validCcRecipients.length > 0) {
                    helper.setCc(validCcRecipients);
                    logger.info("Added {} CC recipients to emergency alert", validCcRecipients.length);
                }
            }

            mailSender.send(message);
            logger.info("EMERGENCY email sent successfully to: {} with CC recipients", to);
        } catch (MessagingException e) {
            logger.error("Failed to send emergency email to: " + to, e);
            throw new RuntimeException("Failed to send emergency email", e);
        }
    }

    /**
     * DEPRECATED: Old plain text welcome email - Use sendBilingualWelcomeEmail instead
     */
    @Deprecated
    public void sendWelcomeEmail(UserDTO user, String temporaryPassword) {
        String subject = "Welcome to Smart Agriculture Management System";
        String body = String.format(
                "Dear %s,\n\n" +
                        "Welcome to the Smart Agriculture Management System! Your account has been created with the following details:\n\n" +
                        "Username: %s\n" +
                        "Email: %s\n" +
                        "Role: %s\n" +
                        "Temporary Password: %s\n\n" +
                        "Please log in and change your password immediately for security purposes.\n\n" +
                        "You can access the system at: [Your Application URL]\n\n" +
                        "Best regards,\nThe Smart Agriculture Management Team",
                user.getFullName(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                temporaryPassword
        );

        sendEmail(user.getEmail(), subject, body);
        logger.info("Welcome email sent to new user: {} ({})", user.getEmail(), user.getRole());
    }

    /**
     * Overloaded method to handle both UserDTO and generic user info for OTP
     */
    public void sendOtpEmail(String fullName, String email, String otp) {
        String subject = "Smart Agriculture Login Verification Code";
        String body = String.format(
                "Dear %s,\n\n" +
                        "Your login verification code for Smart Agriculture Management System is:\n\n" +
                        "%s\n\n" +
                        "This code will expire in 5 minutes.\n\n" +
                        "If you did not attempt to log in, please ignore this email and contact support.\n\n" +
                        "Best regards,\nThe Smart Agriculture Security Team",
                fullName,
                otp
        );

        sendEmail(email, subject, body);
        logger.info("OTP email sent to: {}", email);
    }





}