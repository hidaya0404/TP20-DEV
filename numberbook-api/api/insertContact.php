<?php

ini_set('display_errors', 0);
error_reporting(E_ALL);

header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type, Authorization");

require_once __DIR__ . '/../service/ContactService.php';

try {
    if ($_SERVER["REQUEST_METHOD"] !== "POST") {
        http_response_code(405);

        echo json_encode([
            "success" => false,
            "message" => "Méthode non autorisée. Utilisez POST."
        ]);

        exit;
    }

    $input = file_get_contents("php://input");
    $data = json_decode($input, true);

    if ($data === null) {
        http_response_code(400);

        echo json_encode([
            "success" => false,
            "message" => "Données JSON invalides"
        ]);

        exit;
    }

    $service = new ContactService();

    if (isset($data["contacts"]) && is_array($data["contacts"])) {
        $response = $service->addContacts($data["contacts"]);
    } else {
        $response = $service->addContact($data);
    }

    echo json_encode($response);

} catch (Throwable $e) {
    http_response_code(500);

    echo json_encode([
        "success" => false,
        "message" => "Erreur interne du serveur",
        "error" => $e->getMessage()
    ]);
}