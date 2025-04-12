package com.afdgraph.report;

import com.afdgraph.models.LexicalException;
import com.afdgraph.models.Token;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ReportGenerator {

    public static void generateHtmlReport(List<Token> tokens, List<LexicalException> errors, String outputPath) throws IOException {
        StringBuilder html = new StringBuilder();

        html.append("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <title>Lexical Analysis Report</title>
                <style>
                    body { 
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        background: linear-gradient(120deg, #0f2027, #203a43, #2c5364);
                        color: #f0f0f0; 
                        padding: 20px;
                        min-height: 100vh;
                        margin: 0;
                    }
                    .container {
                        max-width: 1200px;
                        margin: 0 auto;
                        padding: 20px;
                    }
                    h1 { 
                        color: #dff9fb; 
                        text-align: center;
                        font-size: 2.5rem;
                        margin-bottom: 1.5rem;
                        text-shadow: 0 0 15px rgba(223, 249, 251, 0.4);
                    }
                    .table-container {
                        position: relative;
                        transition: transform 0.3s ease;
                        margin-bottom: 30px;
                        backdrop-filter: blur(8px);
                        background: rgba(255, 255, 255, 0.1);
                        border-radius: 15px;
                        padding: 20px;
                        box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
                    }
                    .table-container:hover {
                        transform: translateY(-5px);
                    }
                    table { 
                        width: 100%; 
                        border-collapse: collapse; 
                        background: rgba(20, 20, 20, 0.4);
                        border-radius: 8px;
                        overflow: hidden;
                    }
                    th, td { 
                        border: 1px solid rgba(255, 255, 255, 0.1); 
                        padding: 12px; 
                        text-align: left; 
                    }
                    th { 
                        background: linear-gradient(90deg, #3498db, #2980b9); 
                        color: #fff;
                        font-weight: 600;
                    }
                    tr:nth-child(even) { 
                        background-color: rgba(44, 44, 44, 0.4); 
                    }
                    tr:hover { 
                        background-color: rgba(80, 80, 80, 0.3); 
                    }
                    h2 {
                        color: #dff9fb;
                        margin-top: 30px;
                        text-align: center;
                        font-size: 1.8rem;
                    }
                    .no-errors {
                        text-align: center;
                        padding: 20px;
                        background: rgba(46, 204, 113, 0.2);
                        border-radius: 8px;
                        color: #2ecc71;
                        font-weight: 500;
                    }
                </style>
                <script>
                    document.addEventListener('DOMContentLoaded', function() {
                        const tableContainers = document.querySelectorAll('.table-container');
                        
                        document.addEventListener('mousemove', function(e) {
                            const mouseX = e.clientX / window.innerWidth;
                            const mouseY = e.clientY / window.innerHeight;
                            
                            tableContainers.forEach(container => {
                                const offsetX = (mouseX - 0.5) * 10;
                                const offsetY = (mouseY - 0.5) * 10;
                                container.style.transform = `translateX(${offsetX}px) translateY(${offsetY}px)`;
                            });
                        });
                    });
                </script>
            </head>
            <body>
                <div class="container">
                    <h1>Lexical Analysis Report</h1>
                    
                    <h2>Tokens Recognized</h2>
                    <div class="table-container">
                        <table>
                            <tr><th>Type</th><th>Lexeme</th><th>Line</th><th>Column</th></tr>
        """);

        for (Token token : tokens) {
            html.append(String.format(
                    "<tr><td>%s</td><td>%s</td><td>%d</td><td>%d</td></tr>",
                    token.getType(), token.getLexeme(), token.getLine(), token.getColumn()
            ));
        }

        html.append("</table></div>");

        html.append("<h2>Lexical Errors</h2>");

        if (errors.isEmpty()) {
            html.append("<div class=\"no-errors\">No lexical errors found! 👍</div>");
        } else {
            html.append("""
                <div class="table-container">
                    <table>
                        <tr><th>Error</th><th>Line</th><th>Column</th></tr>
            """);

            for (LexicalException error : errors) {
                html.append(String.format(
                        "<tr><td>%s</td><td>%d</td><td>%d</td></tr>",
                        error.getErrorMessage(), error.getLine(), error.getColumn()
                ));
            }

            html.append("</table></div>");
        }

        html.append("""
                </div>
            </body>
            </html>
        """);

        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write(html.toString());
        }
    }
}