#!/bin/bash
# Setup VP MCP server config for Claude Code
# Usage: ./vp-mcp-setup.sh [port]

set -e

PORT="${1:-2026}"
PROJECT_ROOT="$(cd "$(dirname "$0")/../../../.." && pwd)"
MCP_CONFIG="${PROJECT_ROOT}/.mcp.json"

echo "=== VP MCP Setup ==="
echo "Project root: ${PROJECT_ROOT}"
echo "MCP config:   ${MCP_CONFIG}"
echo "Server URL:   http://localhost:${PORT}/sse"
echo

# Check if .mcp.json exists
if [[ -f "$MCP_CONFIG" ]]; then
    echo "Existing .mcp.json found."

    if grep -q "visual-paradigm" "$MCP_CONFIG" 2>/dev/null; then
        echo "[OK] visual-paradigm already configured."
        echo
        echo "To update the port, edit .mcp.json manually."
        exit 0
    fi

    # Merge into existing config using python
    if command -v python3 &>/dev/null; then
        python3 -c "
import json
with open('${MCP_CONFIG}') as f:
    cfg = json.load(f)
cfg.setdefault('mcpServers', {})['visual-paradigm'] = {
    'url': 'http://localhost:${PORT}/sse'
}
with open('${MCP_CONFIG}', 'w') as f:
    json.dump(cfg, f, indent=2)
print('Added visual-paradigm to existing .mcp.json')
"
    else
        echo "[WARN] python3 not found. Please add this to .mcp.json manually:"
        echo
        echo '  "visual-paradigm": {'
        echo "    \"url\": \"http://localhost:${PORT}/sse\""
        echo '  }'
        exit 1
    fi
else
    echo "Creating new .mcp.json..."
    cat > "$MCP_CONFIG" << EOF
{
  "mcpServers": {
    "visual-paradigm": {
      "url": "http://localhost:${PORT}/sse"
    }
  }
}
EOF
    echo "[OK] Created .mcp.json with visual-paradigm config."
fi

echo
echo "=== Setup Complete ==="
echo
echo "Next steps:"
echo "  1. Start VP MCP server:  cd visual-paradigm-mcp-plugin && ./run docker-up"
echo "  2. Verify connection:    .claude/skills/cnpm-vp/scripts/vp-mcp-verify.sh"
echo "  3. Restart Claude Code to pick up the new MCP config"
