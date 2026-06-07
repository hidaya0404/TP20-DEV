<?php

header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET");
header("Access-Control-Allow-Headers: Content-Type, Authorization");

require_once __DIR__ . '/../service/ContactService.php';

if ($_SERVER["REQUEST_METHOD"] !== "GET") {
    http_response_code(405);

    echo json_encode([
        "success" => false,
        "message" => "Méthode non autorisée. Utilisez GET."
    ]);

    exit;
}

$keyword = $_GET["keyword"] ?? "";

$keyword = trim($keyword);

if (empty($keyword)) {
    http_response_code(400);

    echo json_encode([
        "success" => false,
        "message" => "Veuillez saisir un nom ou un numéro de téléphone"
    ]);

    exit;
}

$service = new ContactService();

$response = $service->searchContacts($keyword);

echo json_encode($response);