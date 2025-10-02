import os
import sys
import json
import pathlib

# Ensure we run from the AI directory so relative files resolve
AI_DIR = pathlib.Path(__file__).resolve().parent
os.chdir(AI_DIR)

try:
    import chatbot  # uses system_prompt.txt relative to AI dir
except Exception as e:
    print(json.dumps({
        "error": f"Failed to import chatbot: {e}"
    }))
    sys.exit(1)


def read_prompt() -> str:
    # Prefer stdin content; fallback to first arg
    data = sys.stdin.read().strip()
    if data:
        return data
    if len(sys.argv) > 1:
        return " ".join(sys.argv[1:])
    return ""


def main():
    prompt = read_prompt()
    if not prompt:
        print(json.dumps({"error": "No prompt provided"}))
        sys.exit(2)

    try:
        answer = chatbot.chat_with_llm(prompt)
        print(json.dumps({
            "answer": answer
        }))
    except Exception as e:
        print(json.dumps({
            "error": str(e)
        }))
        sys.exit(3)


if __name__ == "__main__":
    main()


