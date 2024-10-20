import re
import sys


def convert_image_tags(content):
    # Regular expression to match markdown images wrapped in links
    linked_image_pattern = re.compile(r"\[!\[\[([^\]]+)\]\]\([^\)]+\)")
    # Regular expression to match standalone markdown images
    image_tag_pattern = re.compile(r"!\[.*?\]\((.*?)\)(,width=\d+,height=\d+\])?")

    def replace_linked_image(match):
        # Extract the image URL and replace with Obsidian image embed syntax
        image_url = match.group(1)
        return f"![[{image_url}]]"

    def replace_image_tag(match):
        image_url = match.group(1)
        if image_url.startswith("http"):
            # External link
            return f"![{match.group(1)}]({image_url})"
        else:
            # Internal link (Obsidian style)
            return f"![[{image_url}]]"

    # Replace linked images first, then regular images
    content = linked_image_pattern.sub(replace_linked_image, content)
    return image_tag_pattern.sub(replace_image_tag, content)


if __name__ == "__main__":
    # Read from stdin
    content = sys.stdin.read()

    # Process and fix image tags
    fixed_content = convert_image_tags(content)

    # Output the fixed content
    print(fixed_content)
