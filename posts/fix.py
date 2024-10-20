import os
import re
import sys

import yaml


def process_markdown(file_path):
    with open(file_path, "r") as file:
        content = file.read()

    # Extract front matter and body
    front_matter, body = content.split("---", 2)[1:]
    front_matter = yaml.safe_load(front_matter.strip())

    # Extract title
    title_match = re.search(r"^# (.+)", body, re.MULTILINE)
    if not title_match:
        raise ValueError("Title not found in the markdown file.")
    title = title_match.group(1).strip()

    # Create new front matter
    new_front_matter = front_matter.copy()
    new_front_matter["alias"] = os.path.splitext(os.path.basename(file_path))[0]

    # Remove the title from the body
    body = re.sub(r"(?m)^# .+\n", "", body, count=1)

    # Handle externally linked images correctly
    body = re.sub(r"!\[\[(http.*?)\]\]", r"![\1]", body)

    # Replace image syntax
    body = re.sub(r"!\[.*?\]\((.*?)\)", r"![[\1]]", body)
    body = re.sub(r"\[!\[.*?\]\((.*?)\)\((.*?)\)\]", r"![\1](\2)", body)
    body = re.sub(r"!\[.*?\]\((.*?)(?:\..*?)?\]", r"![[\1]]", body)

    # Remove leading '[' from image links
    body = re.sub(r"\[!\[\[", r"![[", body)

    # Create new content
    new_content = (
        f"---\n{yaml.dump(new_front_matter, default_flow_style=False)}---\n{body}"
    )

    # Clean the title for the filename
    clean_title = re.sub(r"[\\/:]", "", title)
    clean_title = re.sub(r"\s+", " ", clean_title)

    # Ensure output directory exists
    output_dir = "out"
    os.makedirs(output_dir, exist_ok=True)

    # Write to new file
    new_file_name = os.path.join(output_dir, f"{clean_title}.md")
    with open(new_file_name, "w") as new_file:
        new_file.write(new_content)

    print(f"Processed file saved as: {new_file_name}")


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python script.py <path_to_markdown_file>")
        sys.exit(1)

    file_path = sys.argv[1]
    process_markdown(file_path)
