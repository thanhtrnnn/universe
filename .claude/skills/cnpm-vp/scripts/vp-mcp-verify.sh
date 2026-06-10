#!/bin/bash
# Verify VP MCP server is running and responsive
# Usage: ./vp-mcp-verify.sh [port]

set -e

PORT="${1:-2026}"
BASE_URL="http://localhost:${PORT}"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

ok() { echo -e "${GREEN}[OK]${NC} $1"; }
fail() { echo -e "${RED}[FAIL]${NC} $1"; }
warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }

echo "=== VP MCP Server Verification ==="
echo "Target: ${BASE_URL}"
echo

# 1. Check if port is open
if command -v curl &>/dev/null; then
    HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" --connect-timeout 3 "${BASE_URL}/sse" 2>/dev/null || echo "000")
    if [[ "$HTTP_CODE" == "200" ]] || [[ "$HTTP_CODE" == "204" ]]; then
        ok "SSE endpoint reachable (HTTP ${HTTP_CODE})"
    elif [[ "$HTTP_CODE" == "000" ]]; then
        fail "Cannot connect to ${BASE_URL} — server not running?"
        echo
        echo "Start with: cd visual-paradigm-mcp-plugin && ./run docker-up"
        exit 1
    else
        warn "SSE endpoint returned HTTP ${HTTP_CODE} (may still work)"
    fi
else
    # Fallback: check port with nc
    if nc -z localhost "$PORT" 2>/dev/null; then
        ok "Port ${PORT} is open"
    else
        fail "Port ${PORT} is not open — server not running?"
        exit 1
    fi
fi

# 2. Check MCP initialization via curl SSE (quick probe)
echo
echo "Checking MCP protocol..."

# Try to get a session by posting initialize
MCP_INIT='{"jsonrpc":"2.0","id":1,"method":"initialize","params":{"protocolVersion":"2024-11-05","capabilities":{},"clientInfo":{"name":"vp-mcp-verify","version":"1.0"}}}'

if command -v curl &>/dev/null; then
    # Post to the messages endpoint (MCP SSE transport)
    RESPONSE=$(curl -s --connect-timeout 5 -X POST \
        -H "Content-Type: application/json" \
        -d "$MCP_INIT" \
        "${BASE_URL}/mcp/messages" 2>/dev/null || echo "")

    if [[ -n "$RESPONSE" ]] && [[ "$RESPONSE" != "" ]]; then
        ok "MCP server responded to initialize"
        # Try to extract server info
        if command -v python3 &>/dev/null; then
            SERVER_NAME=$(echo "$RESPONSE" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('result',{}).get('serverInfo',{}).get('name','unknown'))" 2>/dev/null || echo "")
            SERVER_VER=$(echo "$RESPONSE" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('result',{}).get('serverInfo',{}).get('version','unknown'))" 2>/dev/null || echo "")
            if [[ -n "$SERVER_NAME" ]]; then
                echo "  Server: ${SERVER_NAME} v${SERVER_VER}"
            fi
        fi
    else
        warn "No response from MCP initialize (server may use different transport path)"
    fi
fi

# 3. Check .mcp.json exists
echo
echo "Checking Claude Code config..."
PROJECT_ROOT="$(cd "$(dirname "$0")/../../../.." && pwd)"
MCP_CONFIG="${PROJECT_ROOT}/.mcp.json"

if [[ -f "$MCP_CONFIG" ]]; then
    if grep -q "visual-paradigm" "$MCP_CONFIG" 2>/dev/null; then
        ok ".mcp.json has visual-paradigm entry"
    else
        warn ".mcp.json exists but no visual-paradigm entry"
        echo "  Run: ./scripts/vp-mcp-setup.sh"
    fi
else
    warn ".mcp.json not found at ${MCP_CONFIG}"
    echo "  Run: ./scripts/vp-mcp-setup.sh"
fi

echo
echo "=== Verification Complete ==="
